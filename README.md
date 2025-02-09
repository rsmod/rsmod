# RS Mod
[![revision][rev-badge]][patch] [![license][license-badge]][isc] [![chat][discord-badge]][discord] [![Github Actions][core-ci-badge]][core-ci] [![Github Actions][nightly-ci-badge]][nightly-ci]

RS Mod is a RuneScape game-server emulator that aims to be as mechanically accurate to the original as possible.

## Requirements
This project requires **[Java 11][java] or later**.

_Check out the [Installing Java](#installing-java) section for setup instructions._

## Installation
- ### IntelliJ
    - On the top-left toolbar: _File → New → Project from Version Control_ ([reference](docs/images/setup1.png))
    - In the **Clone Repository** window: _URL → `https://github.com/rsmod/rsmod.git`_ ([reference](docs/images/setup2.png))
    - In the **Open Project** pop-up that appears: _This Window_ or _New Window_ (personal choice) ([reference](docs/images/setup3.png))
    - Once the project loads, you'll see a **Load Gradle Project** option: ([reference](docs/images/setup4.png))
    - Click on **Load Gradle Project** and wait for the process to finish
    - On the top-right **Run / Debug Configurations**: _GameServer_ ([reference](docs/images/setup5.png))
    - Now click on the **_Run 'GameServer'_** button: ([reference](docs/images/setup6.png))
    - The first time running the server will automatically download all the required game files.
    - You will see eventual "progress" output in the console: ([reference](docs/images/setup7.png))
    - Now that your development environment is set up, any following runs will be much faster.
    - _Note: a default "logback" configuration file may have been picked up. To fix the default logging in console, simply restart the server._
- ### CLI
  ```sh
  git clone https://github.com/rsmod/rsmod.git
  cd rsmod
  gradlew install --console=plain && gradlew run --console=plain
  ```

_RS Mod is compatible with [RSProx][rsprox]. It is the most readily-available client to use for the game-server._

_Check out the [Compatible Clients](#compatible-clients) section for more information._

## Installing Java
#### Where to download Java 11:
- **[Adoptium OpenJDK 11 LTS][adoptium-download]** _Recommended option (free & open-source)_
- **[OpenJDK 11][openjdk-download]**
- **[Oracle JDK 11][oracle-download]** _Requires Login_

#### Installing via Package Manager:
- **Linux/macOS:** Using [SDKMAN!][sdkman]
  ```sh
  sdk install java 11.0.20-tem
  ```
- **macOS:** Using [Homebrew][homebrew]
  ```sh
  brew install openjdk@11
  ```
- **Windows**: Using [WinGet][winget]
  ```sh
  winget install --id EclipseAdoptium.Temurin.11.JDK
  ```

## Compatible Clients
We highly suggest using [RSProx][rsprox] as the client-of-choice:

- Free & open-source
- Actively maintained
- Allows inspection of individual packets sent to/from the server
- Essential for developing and verifying game mechanics

This is an invaluable tool for debugging and ensuring accurate game mechanics.

## Contributing
At this time, the project is still in its early stages and not yet ready for external contributions. Before opening it up to contributions, we want to ensure the codebase and API are well-defined and that clear guidelines are in place for contributors.

Stay tuned for updates as the project matures!

## License
RS Mod is available under the terms of the ISC license, which is similar to the 2-clause BSD license. The full copyright notice and terms are available in the [LICENSE][license] file.

## Links
* [Discord][discord]

[isc]: https://opensource.org/licenses/ISC
[license]: https://github.com/rsmod/rsmod/blob/main/LICENSE.md
[license-badge]: https://img.shields.io/badge/license-ISC-informational
[discord]: https://discord.gg/UznZnZR
[discord-badge]: https://img.shields.io/discord/550024461626114053?color=%237289da&logo=discord
[patch]: https://oldschool.runescape.wiki/w/Update:Bounty_Hunter_Changes,_Collection_Log_Updates_%26_Emote_Improvements
[rev-badge]: https://img.shields.io/badge/revision-228-important
[core-ci]: https://github.com/rsmod/rsmod/actions/workflows/core-ci.yml
[core-ci-badge]: https://github.com/rsmod/rsmod/actions/workflows/core-ci.yml/badge.svg?branch=main
[nightly-ci]: https://github.com/rsmod/rsmod/actions/workflows/nightly-ci.yml
[nightly-ci-badge]: https://github.com/rsmod/rsmod/actions/workflows/nightly-ci.yml/badge.svg?branch=main
[java]: https://openjdk.java.net/projects/jdk/11/
[adoptium-download]: https://adoptium.net/temurin/releases/?version=11
[openjdk-download]: https://jdk.java.net/archive/
[oracle-download]: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
[sdkman]: https://sdkman.io/
[homebrew]: https://brew.sh/
[winget]: https://learn.microsoft.com/en-us/windows/package-manager/winget/
[rsprox]: https://github.com/blurite/rsprox
