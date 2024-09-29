# StaffCredits

**StaffCredits** is a plugin designed to help server owners compensate their staff members through an in-game credit system. Staff members can earn credits and submit requests to redeem them for gift cards via Tebex or CraftingStore, making it easy to reward staff for their hard work.

## Features

- **In-Game Credits**: Server staff can earn credits for their efforts.
- **Request Gift Cards**: Staff can submit withdrawal requests to exchange credits for Tebex or CraftingStore gift cards.
- **Staff Verification**: Only higher staff members (admins, etc.) can approve credit withdrawal requests for additional security.

## Installation

1. Download the plugin `.jar` file.
2. Place the `.jar` file into your server's `plugins` folder.
3. Restart the server to generate the default configuration files.
4. Configure the `config.yml` file with your mongodb & redis information

## Commands

- `/staffcredits`: Core command, opens the main credits UI.
- `/staffcredits manage`: Core Command, opens the management menu to accept or decline requests for giftcard withdrawals.
- `/staffcredits history`: Opens the history menu for credit withdrawals (you can filter with a target argument which is optional)
- `/staffcredits set <target> <amount>`: Admin command, sets a targets credits
- `/resetprofile`: A command for me to reset my player profile for debugging + testing

## Permissions

- `staffcredits.credits`: Core permission, allows you to open the main credits UI.
- `staffcredits.history`: Core permission, allows for you to open the staffcredits history UI
- `staffcredits.admin.set`: Permission to set credits for a player.
- `staffcredits.admin.manage`: Permission to set manage staff credit requests.
- `debug.resetprofile`: Permission for debugging mongodb and resetting profiles.
  
## How It Works

1. **Earn Credits**: Staff members earn credits from high ranking staff members through hard work.
2. **Submit Withdrawal Requests**: When a staff member reaches the required credit threshold, they can request a withdrawal.
3. **Approve or Deny Requests**: Higher-level staff can approve or deny these requests. Upon approval, a gift card code is generated via Tebex or CraftingStore.
4. **Receive Gift Card**: Once approved, the staff member will receive a gift card to redeem in Tebex or CraftingStore.

## Future Features

- **Flexible Gift Card Providers**: Supports both **Tebex** and **CraftingStore** for gift card generation.
- **Fully Configurable**: Customize various aspects of the plugin to suit your server's needs.

## License

This plugin is open-sourced under the MIT License.
