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

If you want to check the authenticity of the downloaded file you can do that by running the command `jarsigner -verify -verbose:summary postbox-1.2.8-shaded.jar` in the same folder where `postbox-1.2.8-shaded.jar` is located.

The output of the command should look like this:
```
s      52687 Sun Feb 27 21:44:36 CET 2022 META-INF/MANIFEST.MF
       52849 Sun Feb 27 21:44:36 CET 2022 META-INF/1.SF (and 1 more)
           0 Sun Feb 27 20:44:46 CET 2022 META-INF/ (and 44 more)
sm     10037 Sun Feb 27 20:44:46 CET 2022 META-INF/maven/at.joestr/postbox/pom.xml (and 339 more)

  s = signature was verified 
  m = entry is listed in manifest
  k = at least one certificate was found in keystore

- Signed by "CN=Joel Strasser, O=Joel Strasser, L=Radenthein, ST=Carinthia, C=AT"
    Digest algorithm: SHA-256
    Signature algorithm: SHA384withRSA, 4096-bit key
  Timestamped by "CN=SSL.com Timestamping Unit 2021, O=SSL Corp, L=Houston, ST=Texas, C=US" on Sun Feb 27 20:44:36 UTC 2022
    Timestamp digest algorithm: SHA-256
    Timestamp signature algorithm: SHA256withSHA256withRSA, 3072-bit key

jar verified.

The signer certificate will expire on 2022-09-30.
The timestamp will expire on 2023-09-11.
```

## Build
To build the project you need at least a Java Development Kit (JDK) in version 17 and Maven 3 installed.  

At first get a copy of the source code. Preferrably via `git clone https://github.com/joestrhq/PostBox.git`.  

Initiate a build with `mvn -Dgpg.skip=true clean package`.  

The compiled plugin (`postbox-X.X.X-shaded.jar`) will be available in the `target` folder.
