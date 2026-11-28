# Arabic Transliteration Locale Profiles

> **About this document**
>
> - This file is a **companion** to [`arabic_transliteration.md`](./arabic_transliteration.md). The general rules (the default transliteration, the order of use between locale and default, and the steps for deriving a locale) are there, especially in section 7. If this file conflicts with that document, follow that document or propose a change to one of them.
> - This is **not a recognized standard** and **not a universal rule**. The profiles here apply only in this repository and do not claim that other spellings are wrong.
> - This document states **our position as developers**, not a religious authority, and may be wrong or may change. See [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 1. How it works

- The default transliteration applies to all readers and is the source of truth. A locale profile only adds a **view adapted to the spelling of one language**.
- A language that **has no profile** in this file has no locale transliteration. Readers of that language get the default transliteration (see section 7.2 in `arabic_transliteration.md`). This is not a shortcoming: the default transliteration is designed for readers of many language backgrounds.
- A profile applies only to **Latin-script** locales. Transliteration into other scripts is written directly by native speakers (section 7.4 in `arabic_transliteration.md`).
- Each profile consists of a **character mapping table** and (optionally) a **word exception list**. Characters that are not listed are left as they are in the default transliteration.
- A profile must not conflict with the general rules. The parentheses `(x)`, the definite article, and the other resolutions are already handled by steps 1 to 3 in section 7.3 of the main document. A profile only governs step 3 (word exceptions) and step 4 (character mapping).

## 2. Indonesian (`id`)

**Character mapping**

| Default    | `id`       | Notes                                  |
| ---------- | ---------- | -------------------------------------- |
| dh         | dz         |                                        |
| th         | ts         |                                        |
| sh         | sy         |                                        |
| kh         | kh         | Unchanged                              |
| gh         | gh         | Unchanged                              |
| ṣ, ḍ, ṭ, ẓ | s, d, t, z | Emphatic letters are not distinguished |
| ḥ          | h          |                                        |
| ʿ, ʾ       | '          | Both become the same mark              |
| ā, ī, ū    | aa, ii, uu | Vowel length is preserved              |

**Word exception list**

| Default | `id` | Reason                                    |
| ------- | ---- | ----------------------------------------- |
| anā     | ana  | The alif is only pronounced when stopping |

**Warning:** this mapping removes distinctions between letters (ص and س both become `s`, ح and ه both become `h`, ع and ء both become `'`). In addition, `ts`, `dz`, and `sy` can be misread as two separate letters (for example ت followed by س). Use the Arabic text or the default transliteration to check the original letters.

**Example 1: Salawat Ibrahimiyyah (Sahih Muslim 405)**

| Layer                   | Text                                                                                                                                                                                                                             |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Default transliteration | Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un) |
| Locale `id`             | Allaahumma salli 'alaa Muhammadin wa 'alaa aali Muhammad, kamaa sallayta 'alaa aali Ibraahiim, wa baarik 'alaa Muhammadin wa 'alaa aali Muhammad, kamaa baarakta 'alaa aali Ibraahiima fii l-'aalamiin, innaka hamiidun majiid   |

Note that the parentheses have been resolved: those before a comma or the end of the text are removed (`Muḥammad(in),` becomes _Muhammad,_), and the others are opened (`Muḥammad(in) wa` becomes _Muhammadin wa_). Idgham of tanwin in _ḥamīdun majīd_ has not yet been applied (see the open decisions in the main document).

**Example 2: the definite article and sun letters (shamsiyyah)**

| Layer                   | Text                                  |
| ----------------------- | ------------------------------------- |
| Default transliteration | `lā yaghfir(u) l-dhunūb(a) illā anta` |
| Locale `id`             | `laa yaghfirudz-dzunuuba illaa anta`  |

## 3. Adding a new locale

1. Decide whether the script is Latin (derived by code, section 7.3 of the main document) or not (written by a native speaker, section 7.4 of the main document). Profiles are only created for Latin scripts.
2. Add one language section to this file, with a character mapping table and a word exception list.
3. Decide the spelling for combinations that are potentially ambiguous and write down the warning.
4. Test the conversion with at least one example from section 10 of the main document and one example with a sun letter.
5. Ask for the result to be reviewed by a native speaker and an Arabic reviewer (section 9 of the main document). Treat the result as a **draft** until the review is complete.

## 4. Open decisions

- **Separator for two-letter combinations.** `dz`, `ts`, and `sy` in the `id` profile (as well as `dh`, `th`, `sh`, `kh`, `gh` in the default transliteration) can be read as two separate consonants. Whether to add a separator has not been decided.
- **Where to store the word exception list.** Whether this list is stored in code, in a profile file, or elsewhere has not been decided. Currently the table is only written in this file.
- **Native-speaker reviewer.** Not yet appointed for Indonesian or any other language.
