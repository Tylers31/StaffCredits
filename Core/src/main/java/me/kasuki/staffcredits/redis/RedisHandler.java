package me.kasuki.staffcredits.redis;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;

import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.redis.packet.AbstractPacket;
import me.kasuki.staffcredits.redis.packet.impl.NotificationPacket;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Getter
public class RedisHandler {

    private final StaffCreditsPlugin instance;

    private final JedisPool jedisPool;

    private final Gson gson;

    private final String password;

    private final String channel;

    private final Map<String, Class<?>> packetMap = new HashMap<>();

    private final AtomicLong errors = new AtomicLong(0);
    private final AtomicLong completedCommands = new AtomicLong(0);
    private final AtomicLong lastError = new AtomicLong(-1);
    private final AtomicLong lastCommand = new AtomicLong(-1);

    public RedisHandler(StaffCreditsPlugin instance, String host, int port, String password, String channel) {
        this.instance = instance;
        this.jedisPool = new JedisPool(host, port);
        this.password = password;
        this.channel = channel;

        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        this.registerPackets();
    }

    private void registerPackets() {
        // Notification Packet
        this.registerClass(NotificationPacket.class);
    }

    private void registerClass(Class<?> clazz) {
        this.packetMap.put(clazz.getName(), clazz);
    }

    public void subscribe() {
        new Thread(() -> this.runCommand(jedis -> jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] arguments = message.split(Pattern.quote("||"));
                Class<?> clazz = packetMap.getOrDefault(arguments[0], null);

                if (clazz == null) {
                    return;
                }

                AbstractPacket packet = (AbstractPacket) getGson().fromJson(arguments[1], clazz);

                if (packet == null) {
                    return;
                }

                processReceive(packet);
            }

        }, this.channel))).start();
    }

    public void publish(AbstractPacket packet) {
        packet.onSend();
        this.runCommand(jedis -> jedis.publish(this.channel, packet.getClass().getName() + "||" + this.gson.toJson(packet)));
    }

    public void runCommand(Consumer<Jedis> consumer) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (jedis != null) {
                if (!this.password.isEmpty()) {
                    jedis.auth(password);
                }
                completedCommands.incrementAndGet();
                lastCommand.set(System.currentTimeMillis());

                consumer.accept(jedis);
            }
        } catch (Exception exception) {
            lastError.set(System.currentTimeMillis());
            errors.getAndIncrement();
            exception.printStackTrace();
        }
    }

    private void processReceive(AbstractPacket packet) {
        packet.onReceive();
    }

}