# Arabic Translation Rules (Arabic → Any Language)

> **About this document**
>
> - This is the **main reference** for translating Arabic texts in this repository. When work in this repository disagrees with it, follow this document or propose a change to it.
> - It is **not a recognized standard** and **not a universal rule**. It does not apply outside this repository, and it does not claim that other translation approaches are wrong.
> - It states **our position as the developers**: the choices we make so that translations here stay consistent and open to checking. It carries no religious authority.
> - It can be wrong and it can change. If you find a translation that does not follow these rules, or a rule that should be improved, please help us. See [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

These rules cover translations of hadith, du'a, and salawat. They are language-neutral. Language-specific choices live in the glossary (section 5) and the language profiles (section 6). The Latin transliteration of Arabic text is defined separately in [`arabic_transliteration.md`](./arabic_transliteration.md).

## 1. Principles

1. **Single source: vocalized Arabic text** (with harakat), checked against a reliable reference to the original collection (for example, sunnah.com). Record the collection and hadith number.
2. **Translate directly from Arabic**, never from an English translation or from another language's translation.
3. **Literal, with natural equivalents.** Keep Arabic word order as long as the target language still reads naturally.
4. **One Arabic word = one equivalent** across the repository, following the glossary (section 5).
5. **Helper words go in parentheses `(xxx)`.** Words that exist in the Arabic are written normally. Words added so the sentence reads naturally in the target language are written in parentheses (see section 2).
6. **No interpretation inside the text.** Explanations of meaning go in footnotes or context notes, never in parentheses.
7. **Do not copy other translations.** Publisher, government, or website translations may be used only to check for missed meaning, never as a source of sentences. Every translation in the repository must be original work or carry an explicit, compatible license.

## 2. Parentheses rule `(xxx)`

**Goes in parentheses**

| Type                                         | Example (English)            |
| -------------------------------------------- | ---------------------------- |
| Helper verb needed to carry a noun           | `(send) salawat` for _ṣalli_ |
| Tense marker attached to that helper verb    | `(have sent)` for _ṣallayta_ |
| Omitted referent that Arabic leaves implicit | `(Allah)`, `(they)`          |
| Connector the target grammar requires        | `(so that)`, `(namely)`      |

**Does not go in parentheses**

- Words that exist in the Arabic, even when rendered with more than one word in the target language (for example, Indonesian _ḥamīd_ → "Maha Terpuji").
- Words the target language requires by grammar and that add no meaning of their own: copulas ("You **are** Praiseworthy"), articles, and agreement markers.
- Honorifics (see section 4).

**Never added, with or without parentheses, inside the translation:** explanations of meaning, history, or rulings. These go in footnotes.

**Parentheses in transliteration** have a different function: they mark endings that are pronounced only when connected to the next word (see [`arabic_transliteration.md`](./arabic_transliteration.md)). Wherever both are shown to readers, explain the difference.

## 3. Sentence structure

- **Follow Arabic word order** whenever the target language remains natural.
- **Genitive constructions (idafa)** follow target-language order: _āl Muḥammad_ → "the family of Muhammad".
- **The conjunction `wa` (و)** is translated as "and". Do not drop it at the start of a clause.
- **Repeated prepositions and negations in Arabic are kept**, for example "upon Muhammad and upon the family of Muhammad" (repeating _ʿalā_).
- **Circumstantial clauses (hāl)** are translated with "while" or an equivalent, not merged into two parallel verbs.
- **Asyndetic sequences** (two items in a row with no connector, such as _ḥamīdun majīdun_) are written with a comma and no added conjunction.
- **Punctuation:** commas between clauses, a full stop at the end of the sentence. Punctuation follows the target language, not the Arabic.

## 4. Pronouns, names, and honorifics

| Arabic                   | Indonesian (`id`)                  | English (`en`)                     | Note                                      |
| ------------------------ | ---------------------------------- | ---------------------------------- | ----------------------------------------- |
| _Allāhumma_              | Ya Allah                           | O Allah                            | Vocative                                  |
| _-ka_ (addressing Allah) | Engkau, -Mu                        | You, Your                          | Capitalized                               |
| _-hi_ (3rd person masc.) | -nya                               | his, him                           | Not honorific forms such as _beliau_      |
| _-kum_                   | kamu or kalian                     | you (plural)                       | Pick one per language and stay consistent |
| _Muḥammad_               | Muhammad                           | Muhammad                           | No added "Nabi" or "the Prophet"          |
| _Ibrāhīm_                | Ibrahim                            | Ibrahim                            | No added "Nabi" or "the Prophet"          |
| Narrator names           | Follow `arabic_transliteration.md` | Follow `arabic_transliteration.md` | Example: Abū Masʿūd al-Anṣārī             |

**Honorifics**

- **In the translated text (matn):** not written, unless they exist in the Arabic.
- **In context notes (narration):** the standard honorific symbol (ﷺ) may be used.
- **In presentation:** honorifics may be displayed alongside the text, but they are not part of the translation itself.

This is a rule about keeping the translation faithful to the wording of the source. It is not a position on whether to say or write salawat when the Prophet ﷺ is mentioned.

## 5. Glossary of equivalents

Add a term to this table **before** using it in any translation. When adding a language, add a column.

| Arabic            | Transliteration | Indonesian (`id`)        | English (`en`)        |
| ----------------- | --------------- | ------------------------ | --------------------- |
| اللَّهُمَّ        | Allāhumma       | Ya Allah                 | O Allah               |
| صَلِّ             | ṣalli           | (berilah) shalawat       | (send) salawat        |
| صَلَّيْتَ         | ṣallayta        | (telah memberi) shalawat | (have sent) salawat   |
| بَارِكْ           | bārik           | (berilah) berkah         | (send) blessings      |
| بَارَكْتَ         | bārakta         | (telah memberi) berkah   | (have sent) blessings |
| عَلَى             | ʿalā            | atas                     | upon                  |
| آل                | āl              | keluarga                 | family                |
| كَمَا             | kamā            | sebagaimana              | as                    |
| وَ                | wa              | dan                      | and                   |
| فِي الْعَالَمِينَ | fī l-ʿālamīn    | di seluruh alam          | in the worlds         |
| إِنَّكَ           | innaka          | sesungguhnya Engkau      | Indeed, You           |
| حَمِيد            | ḥamīd           | Maha Terpuji             | Praiseworthy          |
| مَجِيد            | majīd           | Maha Mulia               | Glorious              |
| أَزْوَاج          | azwāj           | istri-istri              | wives                 |
| ذُرِّيَّة         | dhurriyya       | keturunan                | descendants           |
| عَبْد             | ʿabd            | hamba                    | servant               |
| رَسُول            | rasūl           | Rasul                    | Messenger             |

## 6. Language profiles

Each language keeps its own short profile of conventions. A language profile may not contradict sections 1 to 4.

### Indonesian (`id`)

- _Shalawat_ is kept as a loanword, not translated.
- _ʿalā_ → "atas" (default; see open decisions).
- No "lagi" between two attributes: "Maha Terpuji, Maha Mulia".
- _-hi_ → "-nya", not "beliau".
- Divine pronouns capitalized: "Engkau", "-Mu".
- No added "Nabi" before Muhammad or Ibrahim.

### English (`en`)

- _Salawat_ is kept as a loanword, written without "h".
- _ʿalā_ → "upon".
- _innaka_ → "Indeed, You". When English grammar requires a copula ("Indeed, You are"), it is written without parentheses.
- Divine pronouns capitalized: "You", "Your".
- No archaic forms (thee, thou).
- No added "the Prophet" before Muhammad or Ibrahim.

### Adding a new language

1. Add a column to the glossary and fill every existing term.
2. Decide how _salawat_ and other loanwords are written.
3. Decide pronoun conventions (formal or plural "you", capitalization of divine pronouns).
4. Write a short language profile in this section.
5. Have the translations checked by both a native speaker and an Arabic reviewer (see section 8).

## 7. Context notes

Context notes explain the circumstances of a narration. They are **not** a translation of the Arabic text.

- Write them as a short paraphrase of the narration (who asked, what was asked, who narrated it).
- Do not copy the narration from a published translation.
- Narrator names follow `arabic_transliteration.md`.
- The standard honorific symbol (ﷺ) is allowed here.
- Keep the same facts across all languages.

## 8. Review

1. Check the vocalized Arabic against the reference for the original collection.
2. Draft a literal translation using the glossary.
3. Cross-check the meaning against other translations, without copying.
4. Have a person who knows Arabic review it.
5. Treat a translation as a **draft** until that review is done, and do not present it as reviewed.

To propose a change to a rule or a translation, see [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 9. Example: Salawat Ibrahimiyyah (Sahih Muslim 405)

This example is an **excerpt**: the salawat wording only. In the [source](https://sunnah.com/muslim:405), the hadith continues with a short statement about the salam, which is not included here.

**Arabic**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Indonesian (`id`)**

> Ya Allah, (berilah) shalawat atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) shalawat atas keluarga Ibrahim. Dan (berilah) berkah atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) berkah atas keluarga Ibrahim di seluruh alam. Sesungguhnya Engkau Maha Terpuji, Maha Mulia.

**English (`en`)**

> O Allah, (send) salawat upon Muhammad and upon the family of Muhammad, as You (have sent) salawat upon the family of Ibrahim. And (send) blessings upon Muhammad and upon the family of Muhammad, as You (have sent) blessings upon the family of Ibrahim in the worlds. Indeed, You are Praiseworthy, Glorious.

## 10. Open decisions

- **`ʿalā`: "atas" or "kepada" (Indonesian).** Current default: "atas" (literal). "Kepada" may read more naturally to Indonesian readers. Decide and update the glossary if needed.
- **"kamu" or "kalian" for _-kum_ (Indonesian).** Not yet decided.
- **Words with more than one meaning.** "One Arabic word = one equivalent" may not hold when a word means different things in different contexts (for example, _ṣalāh_ as ritual prayer or as salawat). Whether the glossary may list one equivalent per meaning is not decided.
- **Position of "in the worlds" (_fī l-ʿālamīn_).** Kept in Arabic order (after the blessing clause, before _innaka_).
- **Arabic reviewer.** Not yet assigned.
- **Scope for Qur'an translations.** These rules are written for hadith, du'a, and salawat. Whether Qur'an translations follow the same rules or use a separately licensed translation is not decided.
