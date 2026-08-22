# UQX — Native Android Rewards & Self-Custody Wallet App

> **A BNB Smart Chain-first Android ecosystem combining engagement rewards, referral growth, account balances and a real on-device self-custody wallet.**

UQX is the native Android community application for the UQX ecosystem. The app combines a recurring participation/rewards experience with referral growth, account transfers, notifications and a separate non-custodial BNB Smart Chain wallet generated directly on the user's device.

The production application is written with **Kotlin + Jetpack Compose** and uses a Python API backend for account/reward services while keeping self-custody wallet secrets on-device.

---

## Product model

UQX deliberately separates two different value surfaces that are often confusing in token apps:

```text
UQX App
  │
  ├── Account Layer
  │     ├── engagement reward sessions
  │     ├── referral rewards
  │     ├── in-app UQX balance
  │     ├── user-to-user transfers
  │     └── transaction history
  │
  └── Self-Custody Wallet
        ├── BIP39 recovery phrase
        ├── secp256k1 EVM keypair
        ├── BNB Smart Chain address
        ├── direct on-chain UQX balance reads
        ├── presale / vesting position reads
        └── receive / explorer / recovery tools
```

The in-app **Account** balance is a server-accounting surface for rewards and internal transfers.

The **Wallet** mode is a real EVM address controlled by a key generated on the Android device. These are intentionally not presented as the same thing.

---

## BNB Smart Chain-first self-custody wallet

The native wallet uses standard EVM cryptography:

- BIP39 mnemonic generation;
- 128-bit entropy for a 12-word recovery phrase;
- BIP32 key derivation;
- standard EVM path `m/44'/60'/0'/0/0`;
- secp256k1 private/public key material;
- standard Ethereum-compatible address format used by BNB Smart Chain.

Wallet generation happens locally on the user's device.

```text
SecureRandom entropy
      │
      ▼
BIP39 recovery phrase
      │
      ▼
BIP32 master key
      │
      ▼
m/44'/60'/0'/0/0
      │
      ▼
secp256k1 keypair
      │
      ▼
BNB Smart Chain address
```

The generated recovery phrase and private key are not sent to the UQX/Zynost API as part of wallet creation.

---

## On-device wallet storage

Wallet secrets are stored through Android's encrypted preference stack backed by an Android Keystore master key.

The production wallet store keeps:

- wallet address;
- recovery phrase;
- derived private key;
- last wallet-unlock timestamp.

Sensitive wallet preference files are explicitly excluded from Android cloud backup and device-to-device transfer because the encryption keys are device-bound.

That creates an important recovery invariant:

> **The recovery phrase is the user's portable recovery mechanism — not a cloud copy of the encrypted wallet database.**

---

## Recovery phrase protection

The native UI adds multiple layers around recovery-phrase access:

1. the phrase is shown during wallet creation so the user can back it up;
2. later phrase reveal requires device authentication where available;
3. the UI uses an additional verification step before displaying the saved phrase;
4. after a long wallet inactivity period, the wallet can require the full recovery phrase again before returning to the normal wallet view.

These controls are defense-in-depth UX measures around the locally stored secret. They are not presented as a substitute for independent security review.

---

## Direct BNB Chain reads

The wallet's on-chain portfolio does not depend on the rewards backend to invent a balance.

A read-only BNB Smart Chain client performs `eth_call` requests for the user's real address and reads the current:

- UQX BEP-20 balance;
- presale purchased amount;
- claimed amount;
- currently claimable amount;
- vested amount / locked position.

The read-only client cannot sign or broadcast a transaction. If BNB Chain/RPC is unreachable, the UI shows an unavailable/offline state rather than replacing the missing chain state with a fake zero.

---

## Account layer vs. wallet layer

| Capability | UQX Account | Self-Custody Wallet |
|---|---|---|
| Reward-session earnings | Yes | No |
| Referral rewards | Yes | No |
| User-to-user UQX transfer | Server-accounting flow | Separate on-chain capability/roadmap |
| Recovery phrase | No | Yes |
| Private key | No | Device-only |
| BNB Chain address | Not required | Yes |
| Direct chain balance | No | Yes |
| Presale/vesting reads | No | Yes |

This boundary is important for users and reviewers: a custodial/accounting feature should not be mislabeled as a blockchain wallet, and a real self-custody wallet should not silently depend on server custody.

---

## Community mining / engagement rewards

The app includes a recurring 24-hour "mining" experience used for community participation and token distribution.

In this product, **mining is an engagement/reward mechanism — not proof-of-work cryptocurrency mining performed by the phone's CPU or GPU.**

A typical flow is:

```text
User starts reward session
        │
        ▼
Server records active session
        │
        ▼
App displays live countdown / progress
        │
        ▼
Reward becomes account balance
        │
        ▼
History / leaderboard / referral effects update
```

This distinction keeps the product terminology understandable without making a false technical claim about consensus mining.

---

## Referral growth system

The native referral center supports:

- unique referral code;
- referral install link;
- Play Store install attribution;
- automatic referral-code recovery after installation where attribution is available;
- direct social sharing;
- multi-level network statistics;
- referral earnings summaries;
- referred-user activity list;
- rank/tier progress and speed-bonus presentation.

The Android client uses the Play Install Referrer mechanism rather than requiring the user to manually remember a code after installing the application.

Referral is treated as a distribution layer around the application. Sustainable product utility should remain independent from simply recruiting new participants.

---

## Native application architecture

The current Android application uses a native Compose navigation structure with top-level surfaces for:

- Home / rewards dashboard;
- Referral Center;
- Wallet;
- Notifications;
- Profile;
- Leaderboard / history;
- transaction history;
- settings;
- active sessions;
- 2FA setup and recovery.

Guest users can see a read-only dashboard experience, while interactive account actions route to authentication.

---

## Account and device security

Current client-side security controls include:

- encrypted authentication-token storage;
- optional biometric/device authentication on app entry;
- separate device-auth gate for recovery phrase access;
- TOTP two-factor login flow;
- 2FA recovery flow;
- forced minimum-version security update gate;
- HTTPS-only network configuration;
- cleartext traffic disabled;
- sensitive encrypted preferences excluded from backup/transfer;
- release minification / ProGuard configuration.

The app's forced-update mechanism is designed so a build can be blocked when the backend marks it below the minimum supported security version, while a temporary version-check outage does not automatically lock users out of the app.

---

## Network trust boundary

The native app talks to two different classes of systems:

```text
Android device
   │
   ├── HTTPS API ───── account/reward/referral/session services
   │
   └── BNB JSON-RPC ─ read-only public chain state
```

Account data can come from the authenticated backend.

Wallet chain data is read from BNB Smart Chain for the wallet's real EVM address.

Private wallet material remains local to the device in the current wallet-creation/storage design.

---

## BNB ecosystem role

UQX gives the wider Zynost ecosystem a consumer/community entry point on BNB Smart Chain:

```text
Zynost Intelligence
        │
        ├──────── Zynost Wallet / execution direction
        │
        ├──────── Zynost Pay / merchant payments
        │
        └──────── UQX
                    │
                    ├── native Android community app
                    ├── BNB self-custody address
                    ├── BEP-20 token visibility
                    ├── presale/vesting visibility
                    └── referral-driven distribution
```

UQX should be viewed as the community/reward layer of the broader ecosystem rather than the only product.

---

## Technology

- Kotlin
- Jetpack Compose / Material 3
- Retrofit + Moshi
- OkHttp
- AndroidX Security Crypto
- Android Keystore-backed encrypted preferences
- Android Biometric APIs
- Web3j cryptography
- Bouncy Castle
- BIP39 / BIP32 / secp256k1
- Firebase Cloud Messaging
- Play Install Referrer
- BNB Smart Chain JSON-RPC

---

## Public vs. private repository boundary

This repository is a **public architecture and product overview**. The production Android source remains private.

### Public here

- product architecture;
- account-vs-wallet trust boundary;
- wallet cryptographic standards;
- high-level security controls;
- BNB Chain integration model;
- engagement/reward model;
- referral architecture;
- relationship to the wider Zynost ecosystem.

### Kept private

- production application source;
- internal API implementation;
- authentication/session implementation details;
- anti-abuse logic;
- unreleased wallet features;
- production operational configuration;
- user data;
- private backend endpoints/configuration not needed for public review.

**Never commit a seed phrase, private key, access token, API secret, signing credential or user-private information to this repository.**

---

## Security posture

The application already implements meaningful device-side protections, but this repository does **not** claim the wallet is "more secure than MetaMask" or immune to compromise.

Before making comparative security claims, the appropriate evidence would include independent audit results, threat-model review, dependency review, secure-code review, reproducible tests and a responsible disclosure process.

See [`SECURITY.md`](SECURITY.md) for the public security boundary and [`ARCHITECTURE.md`](ARCHITECTURE.md) for the system map.

---

## Status

**Active development / live ecosystem integration.**

The current private Android code includes native rewards sessions, referral flows, internal UQX account transfers, notifications, authentication/2FA, and a real on-device BNB Smart Chain wallet with direct UQX/presale state reads.
