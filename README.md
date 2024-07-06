Sure! Here's an improved version of the README with additional helpful details:

# Minecraft Chat Plugin
### Spigot-based Plugin

A Spigot plugin designed for integrating advanced chat features with bidirectional Discord integration. This plugin is currently a work in progress and not suitable for production use.

## Features

### Banned Words Filtering
A regex-based filtering system that supports both exact match and wildcard matching for a configurable list of banned words. The filtering applies to multiple input areas in Minecraft:

- Chat
- Items renamed in anvils
- Signs
- Books content and signed titles
- Discord messages (see below)

#### Exact Matching
Exact matching blocks and reports words that match the provided value exactly.

**Examples:**
- If the word "mine" is banned:
    - "mine" will be blocked
    - "mined" will not be blocked
    - "mine cart" will be blocked

#### Wildcard Matching
Wildcard matching blocks and reports words wherever the value appears within another word.

**Examples:**
- If the word "rain" is banned:
    - "rain" will be blocked
    - "raining" will be blocked
    - "rainfall" will be blocked
    - "train" will be blocked

### Discord Integration
A bidirectional integration between Minecraft chat and a Discord server.

- **Discord to Minecraft**: Messages sent in a specific Discord channel are forwarded to the Minecraft chat.
- **Minecraft to Discord**: Messages from Minecraft are sent to the specified Discord channel.

Messages sent from Discord are subject to the same banned words filtering as the Minecraft chat.

## Getting Started

### Prerequisites
- A Minecraft server running Spigot
- Java 8 or higher
- A Discord bot with appropriate permissions to read and send messages in your desired channel

### Installation

1. **Download the Plugin**
    - Download the latest version of the plugin jar file from the releases page.

2. **Install the Plugin**
    - Place the downloaded jar file into your server's `plugins` directory.

3. **Configure the Plugin**
    - Start your server to generate the default configuration files.
    - Stop the server and edit the configuration files located in `plugins/MinecraftChat`.

4. **Discord Bot Setup**
    - Set up your Discord bot and obtain the bot token.
    - Configure the bot token and channel IDs in the `discord.yml` configuration file.

5. **Start the Server**
    - Start your Minecraft server again to load the plugin with your configurations.

### Configuration
#### `banned-words.yml`
- **EXACT_BANNED_WORDS**: List of exact words to be banned.
- **WILDCARD_BANNED_WORDS**: List of words for wildcard matching.

#### `discord.yml`
- **TOKEN**: Your Discord bot token.
- **CHAT_CHANNEL_ID**: The ID of the Discord channel for Minecraft chat.
- **REPORT_CHANNEL_ID**: The ID of the Discord channel for reporting banned words violations.

## Usage

- **Chat Filtering**: The plugin will automatically filter chat messages and other inputs based on your configuration.
- **Discord Integration**: Any messages sent in the specified Discord channel will appear in Minecraft chat and vice versa.

## Development
This plugin is under active development. Contributions and feedback are welcome.

### Building the Plugin
1. Clone the repository.
   ```sh
   git clone https://github.com/yourusername/MinecraftChat.git
   ```
2. Navigate to the project directory.
   ```sh
   cd MinecraftChat
   ```
3. Build the project using your preferred build tool (e.g., Maven or Gradle).
   ```sh
   mvn clean install
   ```

## Contributing
1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Submit a pull request with a detailed description of your changes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Support
For support and troubleshooting, please open an issue on the GitHub repository or join our Discord server (link to be added).
