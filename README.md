# Muslim Launcher

> **Most launchers are built to keep you engaged.  
> Muslim Launcher is built to keep you aware.**

Muslim Launcher replaces the front door of your phone with one anchored in
prayer, intention, and muhasabah — no ads, no analytics, no infinite feeds,
no autopilot. The phone is an _amanah_. This launcher treats it that way.

[![Version](https://img.shields.io/badge/version-0.3.0--alpha-blue.svg)]()
[![License](https://img.shields.io/badge/license-GPL--3.0-green.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%208.0%2B-brightgreen.svg)]()

---

## Why this exists

The launcher is not neutral infrastructure. It is the **front door of the
attention economy** — designed by companies whose business model is to
capture, measure, and monetize engagement. The average Muslim's phone
reflects that: hours on social feeds and short video, while the Quran app
sits unopened on page three of the drawer.

The Islamic apps exist. The problem is that they are _peripheral_. The phone's
default state is distraction, and deen is something you visit occasionally.

> The phone has become a mall. The masjid is a small shop in the corner.

**Muslim Launcher exists to change the default** — to make the phone's
environment prayer-anchored, intentional, and aware, rather than a place you
walk through on autopilot.

**The enemy is not a competitor. It is a design philosophy:**
engagement-driven design — infinite feeds, notification pulses, streaks,
recommendation engines, vanity metrics — whose purpose is to maximize
time-in-app and extract attention as a resource.

We fight it by **refusing to become it**, not by decorating it.

---

## What it is

A custom Android launcher that:

- Puts **prayer times and the Hijri date** at the visual center of the home
  screen — not as a widget you add, but as the structure of the environment.
- Lets you **categorize apps by intention and heart-risk**, not by productivity
  scores:
  - `Deen` — Quran, prayer, learning, zakat
  - `Dunya — necessary` — work, banking, family, maps
  - `Dunya — optional` — shopping, news, entertainment
  - `Fitnah risk` — social feeds, dating, short-video, gossip
- Replaces the silent tap with a **niyyah modal** — a short pause that shows
  facts, asks what you are opening the app _for_, and lets you decide.
- Measures usage **in the rhythm of prayer** — "since Asr," not "since midnight."
- Offers gentle, dismissible **post-salah prompts** — one line, no nagging.
- Runs **fully offline**, with **no ads, no analytics, no tracking, no telemetry.**

**The friction is not a block. It is a mirror.** The goal is awareness, not
restriction.

---

## What it is _not_

- ❌ Not an Islamic-themed launcher. The values are in the **structure**, not the wallpaper.
- ❌ Not a screen-time tracker. Time is not the problem — what fills it is.
- ❌ Not a productivity tool. The goal is _barakah_ and presence, not output.
- ❌ Not a parental control app. It does not scold, shame, or force.
- ❌ Not a fatwa engine. It offers reminders, cites sources, and never claims
  religious authority.
- ❌ Not a platform or an ecosystem. It is one app, on your phone, for you.

---

## Principles

These are the constraints behind every design decision. The full version lives
in [`DEVELOPER_CONTRACT.md`](./DEVELOPER_CONTRACT.md) — read that before
contributing.

| Principle                                      | In practice                                                            |
| :--------------------------------------------- | :--------------------------------------------------------------------- |
| **Amanah** — the phone is a trust              | No feature whose success metric is "time in app."                      |
| **Muhasabah** — account before being accounted | The modal is a mirror, not a judge.                                    |
| **Niyyah** — intention precedes action         | The friction is naming your intention.                                 |
| **Wasatiyyah** — moderation, not asceticism    | We never push zero usage. We never shame.                              |
| **Sadd al-dhara'i** — block the means          | Apps are tagged by _heart-risk_, honestly, not declared haram.         |
| **Rhythm of salah**                            | The day is anchored to prayer, not the clock.                          |
| **No extraction**                              | Offline-first. No ads. No analytics. No third-party SDKs that collect. |

---

## What we refuse to ship

This list is the immune system of the project. It does not negotiate.

- Ads, of any kind, ever.
- Analytics, telemetry, or crash reporting that phones home.
- Infinite scroll, feeds, "for you" recommendations, or algorithmic content.
- Streaks, points, badges, leaderboards, or any gamification.
- Notifications designed to pull you back into the app.
- Dark patterns — fake urgency, guilt-tripping, forced onboarding.
- Fatwas, declarations of haram, or claims to religious authority.
- A runtime dependency on a second "services" app.
- Any certification or authentication ecosystem that makes us a gatekeeper.

If a future contributor proposes any of the above, this list is the answer.

---

## Status

**Pre-alpha.** After an initial build and a long pause, the project is being
rebuilt around a sharper thesis and a tighter scope.

**The MVP is a single launcher app.** No companion install. No background
services app. No ecosystem. The launcher must be fully functional on its own,
offline, with only the launcher role granted.

The "Muslim Services" background engine — once part of the architecture — is
**deferred** until there is a proven, standalone reason for it. If it ever
returns, it will be a **build-time library**, not a runtime gate.

---

## Roadmap

The project grows in phases. Each phase is validated before the next begins.

- **Phase 0 — Validate.** Talk to 10 Muslims. Show the modal. Ask: _aware or judged?_
- **Phase 1 — Smallest shippable launcher.** Prayer-anchored home, drawer,
  categorization, niyyah modal. Nothing else.
- **Phase 2 — Prayer-rhythm intelligence.** Prayer-interval stats, post-salah
  prompts, salah-window DND, weekly muhasabah view.
- **Phase 3 — Shared library (only if justified).** Extract prayer / Hijri /
  Islamic data into a Gradle module for reuse by other apps.
- **Phase 4 — Ecosystem (only if traction proves demand).** A directory, not a
  certification system. An open protocol, not a gatekeeper.

No phase is skipped for speed.

---

## Tech stack

- **Kotlin** + **Jetpack Compose** — modern native Android UI.
- **Room** — local storage for settings, app categories, cached stats.
- **WorkManager** + **AlarmManager** — reliable prayer notifications.
- **Local prayer-time calculation** and **local Hijri conversion** — no network.
- **Fully offline-first.** No backend. No account. No cloud.

Optional permissions (DND access, usage stats, notification listener) are
requested **only when a feature needs them**, explained in plain language, and
fully declinable without breaking the app.

---

## Build & run

```bash
git clone https://github.com/SadaqahCode/muslim-launcher.git
cd muslim-launcher
./gradlew assembleDebug
```

Or open the project in Android Studio (latest stable), sync Gradle, and run on
a physical device (a launcher needs a real device to behave correctly — an
emulator will not exercise the launcher role properly).

---

## Contributing

1. Read [`DEVELOPER_CONTRACT.md`](./DEVELOPER_CONTRACT.md) **before** opening a PR.
2. Every PR must state which of the seven tests it passes and how.
3. No new dependency without justification — every dependency is a potential
   data leak and a maintenance burden.
4. No scope creep. If it's outside the current phase, it will be deferred.
5. If you disagree with the contract, open an issue. The contract can change —
   but only through explicit, documented reasoning.

**The one question for every contribution:**

> Does this make the default state of the phone more Islamic, more intentional,
> more prayer-anchored — without guilt, without restriction, and without
> becoming the thing we are fighting?

If yes, it belongs. If not, it doesn't.

---

## License

_To be decided before the first public release._ The intent is a permissive or
copyleft open-source license that keeps the app free of ads and analytics in
all future forks. A license that permits extraction of user data would
contradict the project's reason to exist.

---

## Contact

- **GitHub:** [SadaqahCode](https://github.com/SadaqahCode)
- **Issues:** please open an issue for bugs, proposals, or disagreement with the contract.

---

> **The phone is an _amanah_. This launcher treats it that way.**

---

## 🌙 About Sadaqah Code

**Sadaqah Code** is an Islamic open-source initiative dedicated to building high-quality, free, and accessible digital tools for Muslims worldwide.

We believe that code can be a form of worship when it serves the community. Our mission is to provide transparent, ad-free, and community-owned software that upholds Islamic values.

> **Note:** Sadaqah Code was founded and is maintained by the **[FlagoDNA](https://flagodna.com)** team. While FlagoDNA leads the technical development, Sadaqah Code represents the spirit of community ownership and open-source contribution.

---

**Bismillah.**

_Sadaqah Code Team_  
_Technology for the Ummah_

```

```
