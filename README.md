# NixHome

A simple homes system.

- `/sethome [name]`, `/home [name]`, `/delhome [name]`, `/homes`
- Default home name is `home`
- Max homes per player is set in `config.yml`
- Homes are saved in `plugins/NixHome/homes.yml`

Build: `mvn clean package` -> `target/NixHome-1.0.0.jar`
Requires Java 21 and Paper 1.21.4.
