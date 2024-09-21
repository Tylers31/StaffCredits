package me.kasuki.staffcredits.api.database.mysql;

import me.kasuki.staffcredits.utilities.cuboid.Cuboid;
import me.kasuki.staffcredits.utilities.serialization.CuboidSerializer;
import me.kasuki.staffcredits.utilities.serialization.ItemStackSerializer;
import me.kasuki.staffcredits.utilities.serialization.LocationSerializer;
import me.kasuki.staffcredits.utilities.serialization.PotionEffectSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMySQLRepository<K, V> {

    private final Gson gson;

    private final HikariDataSource dataSource;
    private final String tableName;
    private final Type valueType;

    public AbstractMySQLRepository(HikariDataSource dataSource, String tableName, Type valueType) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.valueType = valueType;
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectSerializer())
                .registerTypeHierarchyAdapter(Location.class, new LocationSerializer())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(Cuboid.class, new CuboidSerializer())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        this.setupTable();
    }

    private void setupTable() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.tableName + " (data_key VARCHAR(255) PRIMARY KEY, json_data LONGTEXT)")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBatch(Map<K, V> batch) {
        CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {
                String query = "INSERT INTO " + this.tableName + " (data_key, json_data) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE json_data = VALUES(json_data)";
                PreparedStatement statement = connection.prepareStatement(query);

                for (Map.Entry<K, V> entry : batch.entrySet()) {
                    String key = entry.getKey().toString();
                    V value = entry.getValue();
                    statement.setString(1, key);
                    statement.setString(2, this.gson.toJson(value));
                    statement.addBatch();
                }

                statement.executeBatch();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void deleteBatch(List<K> batch) {
        CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {
                String query = String.format("DELETE FROM %s WHERE data_key = ?", this.tableName);
                PreparedStatement statement = connection.prepareStatement(query);
                for (K key : batch) {
                    statement.setString(1, key.toString());
                    statement.addBatch();
                }

                statement.executeBatch();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void saveToDatabase(K key, V value) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.tableName + " (data_key, json_data) VALUES (?, ?)")) {
            statement.setString(1, key.toString());
            statement.setString(2, this.gson.toJson(value));
            statement.executeUpdate();
        }
    }

    public List<V> getAllEntriesFromDatabase() {
        List<V> allValues = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT json_data FROM " + this.tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String jsonData = resultSet.getString("json_data");
                    V value = this.gson.fromJson(jsonData, this.valueType);
                    allValues.add(value);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return allValues;
    }

    public Optional<V> getFromDatabase(K key) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT json_data FROM " + this.tableName + " WHERE data_key = ? LIMIT 1")) {
            statement.setString(1, key.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String jsonData = resultSet.getString("json_data");
                    return Optional.ofNullable(this.gson.fromJson(jsonData, this.valueType));
                }
                return Optional.empty();
            }
        }
    }

    public void removeFromDatabase(K key) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.tableName + " WHERE data_key = ?")) {
            statement.setString(1, key.toString());
            statement.executeUpdate();
        }
    }

    public void replaceInDatabase(K key, V value) throws SQLException {
        this.removeFromDatabase(key);
        this.saveToDatabase(key, value);
    }

    public boolean existsInDatabase(K key) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + this.tableName + " WHERE data_key = ?")) {
            statement.setString(1, key.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }
}
