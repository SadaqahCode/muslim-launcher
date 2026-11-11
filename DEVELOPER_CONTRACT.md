# DEVELOPER_CONTRACT.md

> **This document is the constitution of the project.**
> It defines what we build, why we build it, and — more importantly — what we **refuse** to build.
> Every feature, every PR, every collaborator, every future decision must pass the tests written here.
> If a feature cannot pass these tests, it does not ship — no matter how good it looks.

---

## 1. Why this project exists

The launcher is not neutral infrastructure. It is the **front door of the attention economy** — designed by companies whose business model is to capture, measure, and monetize engagement. Most Muslims use these launchers by default, and the launcher's design quietly teaches them to relate to their phone as a **consumption device**, not as an **amanah**.

**Muslim Launcher exists to replace that front door with one built on different values:**

- intention over engagement
- prayer over notifications
- awareness over autopilot
- muhasabah over metrics
- the courtyard over the mall

We are not building "an Islamic-themed launcher."
We are not building "a screen-time tracker."
We are not building "a productivity tool."

We are building a **different relationship between the user and their phone** — anchored in prayer, intention, and self-accounting, and explicitly opposed to engagement-driven design.

---

## 2. The enemy

We have one enemy, and it is not a competitor. It is a **design philosophy**:

> **Engagement-driven design** — the set of dark patterns, infinite feeds, notification pulses, recommendation engines, streak mechanics, and vanity metrics whose purpose is to maximize time-in-app and extract user attention as a resource.

This enemy is not "non-Muslim launchers."
It is not "capitalism" in the abstract.
It is not "technology."

It is specifically: **design that treats the user as a resource to be extracted rather than a trust to be honored.**

We fight it by **refusing to become it** — not by adding Islamic decoration to the same architecture.

---

## 3. Core values → design constraints

These are not slogans. They are **constraints** that produce concrete design decisions. Each value must be traceable to a feature, a refusal, or an architectural choice.

| Value                                    | Meaning                                                            | Design consequence                                                                                                                              |
| :--------------------------------------- | :----------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------- |
| **Amanah** (trust)                       | The phone is a trust from Allah, not a possession to be maximized. | The launcher serves the user's intention, never the app's engagement. No feature whose success metric is "time in app."                         |
| **Muhasabah** (self-accounting)          | Account yourselves before you are accounted.                       | The friction modal is a mirror, not a judge. It shows facts, asks intention, and lets the user decide.                                          |
| **Niyyah** (intention)                   | Intention precedes action.                                         | The primary friction is **naming the intention**, not blocking the action.                                                                      |
| **Wasatiyyah** (moderation)              | Neither extravagant nor niggardly — a just balance.                | We never push zero usage. We never shame. The goal is balance, not asceticism.                                                                  |
| **Sadd al-dhara'i** (blocking the means) | Block the path to harm, not just the harm itself.                  | Distracting apps are categorized by **heart-risk**, not by productivity. The framing is honest: "this may harm the heart," not "this is haram." |
| **The rhythm of salah**                  | The day is anchored to prayer, not the clock.                      | Usage is measured "since Asr," not "today." Post-salah prompts. Salah-window DND.                                                               |
| **Leaving what doesn't concern you**     | Part of perfecting Islam is leaving what doesn't concern you.      | The launcher offers a **tag**, not a lecture: "is this among what concerns you today?"                                                          |

**We do not issue fatwas.** We use verified scholarly content, cite sources, and label everything as a **reminder**, not a ruling.

---

## 4. The tests — every feature must pass

Before any feature ships, it must pass **all** of these tests. If it fails one, it does not ship.

### Test 1 — Stance

> **Does this feature serve the user's intention, or does it serve engagement?**

If the feature increases time-in-app, opens a feed, recommends content, or rewards frequent use → **it serves engagement → reject.**

### Test 2 — Amanah

> **Would I be comfortable explaining this feature to a user as a trust?**

If the honest explanation includes "we collect," "we track," "we monetize," or "we optimize for retention" → **it violates amanah → reject.**

### Test 3 — No extraction

> **Does this feature collect any data we do not strictly need to function offline?**

If yes → **remove the collection.** No ads. No analytics. No telemetry. No crash reporting that phones home. No "anonymous usage stats." No third-party SDKs that collect anything.

### Test 4 — No addiction mechanics

> **Does this feature reward frequent use?**

Points. Streaks. Badges. Daily check-ins. Leaderboards. "You're on a 7-day streak!" — **all rejected.** These are engagement design in Islamic clothing. They extract the same dopamine the enemy extracts.

### Test 5 — Degradation

> **Does the app work without optional permissions?**

The launcher must be fully functional with **only** the launcher role. Every optional permission (DND, usage stats, notification listener) must degrade gracefully — never block the app, never nag, never guilt the user into granting.

### Test 6 — Humility

> **Am I issuing a ruling, or offering a tool?**

We offer tools and reminders. We do not declare apps haram. We do not speak for scholars. We do not present our framing as the only valid Islamic position.

### Test 7 — Scope

> **Is this MVP, or am I rebuilding the gate?**

A "Muslim Services" runtime dependency, a certification ecosystem, an authentication layer, a data gate — **all deferred indefinitely.** If the feature only makes sense in the context of a future ecosystem that doesn't exist yet → **it does not ship today.**

---

## 5. What we refuse

This list is as important as the feature list. It is the **immune system** of the project.

We **refuse** to ship:

- ❌ Ads, of any kind, ever.
- ❌ Analytics, telemetry, crash reporting that phones home, or third-party SDKs that collect data.
- ❌ Infinite scroll, feeds, "for you" recommendations, or algorithmic content.
- ❌ Streaks, points, badges, leaderboards, or any gamification.
- ❌ Notifications designed to pull the user back into the app.
- ❌ Dark patterns of any kind — fake urgency, guilt-tripping, forced onboarding, hidden settings.
- ❌ Fatwas, declarations of haram, or claims to religious authority.
- ❌ A runtime "services" dependency that gates the launcher behind a second app.
- ❌ A certification or authentication ecosystem that makes us the gatekeeper.
- ❌ Any feature whose success metric is "time in app" or "daily active users."

If a future contributor proposes any of the above, this document is the answer. **We do not debate the immune system.**

---

## 6. Architecture principles

These are the technical commitments that keep the values honest.

### 6.1 — One app, for now

The MVP is a **single, combined launcher app.** No separate services app. No runtime gate. The launcher must be fully functional on its own.

If the data/logic ever needs to be shared with other apps, it will be extracted as a **Gradle library** (build-time dependency), never as a **runtime dependency**. Build-time sharing does not gate the user; runtime sharing does.

### 6.2 — Offline-first, always

The app must be fully functional **with no network access.** Prayer times are calculated locally. Hijri dates are computed locally. Quran/Hadith content is bundled or cached. Usage stats are stored locally. Nothing phones home.

If a feature requires network access, it must be **optional** and must degrade gracefully.

### 6.3 — Modular core, so we can split later

The prayer calculation, Hijri calendar logic, and Islamic data should live in `core/` packages with no UI dependencies. This is so that:

1. The code stays testable.
2. If we ever need to extract a shared library, the extraction is clean.
3. The launcher UI remains lightweight and responsive.

**But we do not split the app. We only keep the code modular.**

### 6.4 — Permissions are earned, never demanded

Every optional permission must be:

- Explained in plain language before the request.
- Requested only when the user triggers the feature that needs it.
- Fully declinable without breaking the app.
- Never re-prompted with guilt or nagging.

### 6.5 — Performance is part of the values

The friction modal must appear in **under 300ms**. A slow modal is a broken mirror — it turns muhasabah into annoyance. Cache usage stats locally. No network calls in the modal. No blocking work on the main thread.

### 6.6 — No third-party SDKs that collect data

Firebase Analytics, Crashlytics, Facebook SDK, TikTok SDK — **none of these.** If a crash reporter is ever needed, it must be opt-in, self-hosted, and documented in the README.

---

## 7. Scope discipline

The project grows in **phases**, and each phase must be **validated before the next begins.**

### Phase 0 — Validate (before any new code)

- Write the manifesto (this document + README).
- Talk to 10 Muslims. Show the modal sketch. Ask: _"aware or judged?"_
- Decide: proceed, revise, or stop.

### Phase 1 — Smallest shippable launcher

- Home screen with prayer times + Hijri date at the center.
- App drawer + search.
- Four-tag categorization.
- Niyyah modal.
- **No usage stats. No services app. No ecosystem.**

### Phase 2 — Prayer-rhythm intelligence

- Prayer-interval usage stats.
- Post-salah prompts.
- Salah-window DND.
- Weekly muhasabah view.

### Phase 3 — Shared library (only if justified)

- Extract `core/prayer`, `core/hijri`, `core/quran-data` into a Gradle module.
- Only if other apps need it. Not before.

### Phase 4 — Ecosystem (only if traction proves demand)

- A **directory**, not a certification system.
- An **open protocol**, not a gatekeeper.
- If we ever become the gatekeeper, we have lost.

**No phase may be skipped. No phase may be merged with another for speed.**

---

## 8. Contributor rules

For anyone who joins this project — including future you, reading this six months from now:

1. **Read this document before opening a PR.** If your PR violates any test or refusal in this document, it will be rejected, regardless of code quality.
2. **Cite the test.** Every PR description must state which of the seven tests it passes and how.
3. **No scope creep.** If your PR adds a feature outside the current phase, it will be split or deferred.
4. **No new dependencies without justification.** Every dependency is a potential data leak, a potential maintenance burden, and a potential violation of the offline-first principle. Justify it in the PR.
5. **No "just this once."** The immune system does not negotiate. A single ad, a single analytics SDK, a single streak — these are not small exceptions. They are the beginning of becoming the enemy.
6. **Disagree with the document? Open an issue.** The document can change — but only through explicit, documented reasoning, not through a silent PR.

---

## 9. Honest tensions (what we do not claim)

We do not pretend to be free of the ecosystem. We are honest about what we are:

- **We ship on Google Play.** The store's economics reward engagement. We use it because that's where users are, but the app itself refuses engagement mechanics.
- **We run on Android.** Android is Google's platform. We do not pretend to escape it. We replace one link in the chain — the front door.
- **We are not anti-technology.** We are not asking users to throw away their phones. We are asking: _whose interests should the phone serve?_
- **We are not scholars.** We are developers building a tool informed by Islamic values. We cite sources. We do not issue rulings.
- **We may be wrong.** The thesis may fail. The niche may be too small. The tone may not land. We ship small, measure honestly, and revise — or stop — based on evidence, not hope.

---

## 10. The one-sentence test (for when this document is too long)

When in doubt, return to this:

> **Does this feature make the default state of the phone more Islamic, more intentional, more prayer-anchored — without guilt, without restriction, and without becoming the thing we are fighting?**

If yes → it belongs.
If no → it does not.

---

_This document is a living contract. It may be amended, but only with explicit reasoning, documented in an issue, and agreed upon before any code is merged. It is not a suggestion. It is the boundary of the project._
