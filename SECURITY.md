# UQX App — Security Overview

This public document describes the security boundary of the UQX native Android application at a high level. It is intentionally not a dump of production secrets, internal defensive thresholds or private implementation details.

## Security principles

The production Android client is designed around these principles:

1. **Self-custody wallet secrets stay on the user's device.**
2. **Encrypted preference blobs that depend on Android Keystore keys are not backed up or transferred.**
3. **Recovery-phrase access receives stronger protection than ordinary UI access.**
4. **Blockchain reads fail unavailable rather than silently inventing balances.**
5. **Authentication, 2FA and entitlement decisions are enforced by the backend rather than only by the client UI.**
6. **Cleartext HTTP is disabled.**
7. **Security-critical client versions can be retired through a minimum-version gate.**

## Wallet key material

The wallet uses a locally generated BIP39 mnemonic and secp256k1 keypair. The current wallet creation/storage path does not upload the mnemonic or private key to the UQX/Zynost backend.

Wallet material is stored through Android encrypted preferences backed by a Keystore master key.

Sensitive wallet preference files are excluded from Android backup and device transfer because encrypted blobs should not be restored without the device-bound Keystore material used to protect them.

## Recovery phrase

Recovery-phrase display is treated as a high-risk action. The native application places device authentication and additional user verification around later reveal flows.

Users remain responsible for securely backing up their recovery phrase. Loss of both the device and recovery phrase may make self-custody funds unrecoverable.

## Network security

The production Android manifest/network security configuration disables cleartext traffic and uses HTTPS for backend API communication.

Read-only BNB Smart Chain state is obtained through public JSON-RPC calls. RPC availability is not treated as proof that a wallet balance is zero; unavailable chain data is surfaced as unavailable/offline state.

## Authentication

The broader account system includes email authentication, 2FA flows, active-session controls and device re-authentication options.

Wallet private keys are distinct from application-account authentication credentials. Compromise of an account credential should not be described as automatically equivalent to possession of the on-device wallet key.

## Public disclosure boundary

This repository may document:

- cryptographic standards;
- trust boundaries;
- BNB integration architecture;
- backup policy;
- high-level authentication controls;
- security assumptions and limitations.

It should not publish:

- real seed phrases or private keys;
- access tokens;
- production credentials;
- internal anti-abuse thresholds;
- sensitive backend configuration;
- user data;
- unreleased exploitation details that would materially increase risk to live users.

## Responsible disclosure

If you believe you have found a security issue, do not publish exploit instructions or affected user data in a public issue. Contact the project privately through the official Zynost/UQX support/security channel and include enough information to reproduce the issue safely.

## No absolute security claim

The presence of Android Keystore, encrypted local storage, biometrics, 2FA or other defenses does not make the application immune to compromise. Independent security review, dependency review, threat modelling and continuous hardening remain necessary for a wallet application.
