# PostBox
![Build Status](https://shields.io/endpoint?url=https://files.joestr.at/ci-build-status/cctray.php?project_name=PIP.github.joestrhq.PostBox_master)
[![Maintainability](https://api.codeclimate.com/v1/badges/1db7872f0dc6536049c6/maintainability)](https://codeclimate.com/github/joestrhq/PostBox/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/1db7872f0dc6536049c6/test_coverage)](https://codeclimate.com/github/joestrhq/PostBox/test_coverage)
[![License](https://img.shields.io/static/v1?label=License&message=MIT&color=blue)](https://github.com/joestrhq/PostBox/blob/master/LICENSE)
[![Matrix](https://img.shields.io/matrix/joestrhq.general:matrix.org?color=0dbd8b&logo=matrix)](https://matrix.to/#/#joestrhq.general:matrix.org)

## About
PostBox is a Spigot plugin, which gives you the possibility to send items to other players.

## Installation

Head over to the [latest release](https://github.com/joestrhq/PostBox/releases/tag/v1.2.8).

Download the `jar` file (currently `postbox-1.2.8-shaded.jar`).

Drop the downloaded `jar` file into your Spigot `plugins` folder.

Stop your Spigot server and start it again.

### Authenticity
If you want to check the authenticity of the downloaded file you can do that by verifying the GPG signature.

**Ubuntu**
```
$ curl -sSL "https://www.joestr.at/assets/software_joestr.at_0x3866986c_public.asc" | gpg --import - # Import the key used to sign the software
gpg: directory '/root/.gnupg' created
gpg: keybox '/root/.gnupg/pubring.kbx' created
gpg: /root/.gnupg/trustdb.gpg: trustdb created
gpg: key 949F21403866986C: public key "joestr.at (Software signing) <email@joestr.at>" imported
gpg: Total number processed: 1
gpg:               imported: 1
$ curl -sSL "https://github.com/joestrhq/PostBox/releases/download/v1.2.8/postbox-1.2.8-shaded.jar.asc" # Download the signature file
$ gpg --verify postbox-1.2.8-shaded.jar.asc postbox-1.2.8-shaded.jar # Verfiy signature
gpg: Signature made Thu 02 Nov 2023 11:28:33 PM CET
gpg:                using RSA key 0B3BC83B18C12C83948D5CF5949F21403866986C
gpg: Good signature from "joestr.at (Software signing) <email@joestr.at>" [unknown]
gpg: WARNING: This key is not certified with a trusted signature!
gpg:          There is no indication that the signature belongs to the owner.
Primary key fingerprint: 0B3B C83B 18C1 2C83 948D  5CF5 949F 2140 3866 986C
```

## Build
To build the project you need at least a Java Development Kit (JDK) in version 17 and Maven 3 installed.  

At first get a copy of the source code. Preferrably via `git clone https://github.com/joestrhq/PostBox.git`.  

Initiate a build with `mvn -Dgpg.skip=true clean package`.  

The compiled plugin (`postbox-X.X.X-shaded.jar`) will be available in the `target` folder.
