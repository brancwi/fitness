# Clean Code — Software Craftsmanship Guide

A comprehensive reference inspired by *Clean Code* (Robert C. Martin) and modern software craftsmanship practices. Use this skill whenever you write, review, or refactor code to ensure it is readable, maintainable, testable, and professional.

---

## When to Use This Skill

- Writing new code (functions, classes, modules, APIs)
- Refactoring existing code
- Code reviews (peer or self-review)
- Designing architectures or choosing abstractions
- Setting up a new project or onboarding a team
- Debugging or investigating bugs (smells often reveal root causes)

---

## 1. Project Setup & Team Standards

Before writing code, establish a foundation that enforces quality automatically.

### 1.1 Version Control
- Use Git with a clear branching strategy (e.g., GitFlow, trunk-based, or GitHub Flow).
- Write meaningful commit messages:
  - Use the imperative mood (`Add feature` not `Added feature`).
  - First line ≤ 50 characters, then a blank line, then detailed body.
  - Reference issue/ticket numbers when applicable.

### 1.2 Build & CI/CD
- **One-step build**: A single command must compile, package, and validate the project.
- **One-step test**: A single command must run the entire test suite.
- Set up Continuous Integration (CI) to run build + tests + static analysis on every push.
- Fail the build on:
  - Compilation errors
  - Test failures
  - Linter/formatting violations (or auto-fix them)
  - Coverage regression (optional but recommended)

### 1.3 Tooling (Automated Enforcement)
| Concern | Examples |
|---|---|
| Formatting | Prettier, Black, rustfmt, gofmt, spotless |
| Linting | ESLint, Pylint, Checkstyle, SonarQube, detekt |
| Static Analysis | SonarQube, CodeClimate, Semgrep |
| Type Checking | TypeScript, mypy, Kotlin compiler strict mode |
| Pre-commit hooks | husky, pre-commit, lint-staged |

> **Rule**: Never rely on manual discipline alone. Automate standards so the codebase stays clean without constant nagging.

### 1.4 Project Structure
- Organize by feature/domain, not by layer (e.g., `orders/OrderService.kt`, not `services/OrderService.kt`).
- Keep related files close together.
- Separate public API from internal implementation details.
- Use clear directory boundaries for: `src`, `tests`, `docs`, `config`, `scripts`.

---

## 2. Meaningful Names

Names are the primary way you communicate intent to other developers (including your future self).

### 2.1 Core Rules
- **Reveal intent**: The name should answer why it exists, what it does, and how it is used.
  - ❌ `int d;`
  - ✅ `int elapsedTimeInDays;`
- **Avoid disinformation**: Do not use names that imply the wrong type or behavior.
  - ❌ `accountList` when it is actually a `Set`.
- **Make meaningful distinctions**: Do not use noise words.
  - ❌ `moneyAmount` vs `money`, `theMessage` vs `message`, `nameString` vs `name`.
  - ✅ Use semantic differences: `customerAddress` vs `customerBillingAddress`.
- **Use pronounceable names**: Humans use words to think.
  - ❌ `genymdhms` (generation date, year, month, day, hour, minute, second)
  - ✅ `generationTimestamp`
- **Use searchable names**: Single-letter names and numeric constants are hard to find.
  - ❌ `if (x == 5)`
  - ✅ `if (status == WORK_DAYS_PER_WEEK)`
- **Avoid encodings**:
  - No Hungarian notation (`strName`, `nCount`).
  - No member prefixes (`m_`, `_`, `f_`).
  - No interface prefixes (`IUser` → just `User` and `UserImpl`).
- **Avoid mental mapping**: The reader should not have to translate names in their head.
  - ❌ `r` for a URL, `c` for a customer.

### 2.2 Class Names
- Use nouns or noun phrases: `Customer`, `WikiPage`, `AccountParser`.
- Avoid vague names like `Manager`, `Processor`, `Data`, `Info` unless they are domain-specific.

### 2.3 Method Names
- Use verbs or verb phrases: `postPayment`, `deletePage`, `save`, `isValid`.
- Accessors/mutators/predicates: `getX`, `setX`, `isX`, `hasX`.
- If a method returns a transformed value, name it after the transformation:
  - ✅ `transformedCoordinates()` rather than `getCoordinates()` when computation is involved.

### 2.4 Consistency
- **One word per concept**: Pick one term and stick to it.
  - ❌ Mixing `fetch`, `get`, `retrieve`, `find` for the same concept.
  - ✅ Choose one and use it everywhere.
- **Do not pun**: Do not use the same word for two different concepts.
- **Use problem domain names**: When possible, use terms from the business domain.
- **Use solution domain names**: For technical concepts (algorithms, patterns), use CS terms.

### 2.5 Scope & Length
- Short names for short scopes (`i` in a 3-line loop is fine).
- Long, descriptive names for long scopes or public APIs.

---

## 2b. Magic Numbers, Magic Strings & Configuration

> Every raw literal must justify its existence. If it cannot, replace it.

### The Decision Hierarchy
When you encounter a raw literal (number or string) in code, apply this hierarchy **in order**:

1. **Configuration (preferred)** — Can this value change per environment, tenant, or deployment?
2. **Enumeration** — Does this represent a member of a closed set of semantic values?
3. **Named Constant** — Is this a universal or technical constant?
4. **If none of the above, refactor the expression** so the literal disappears into a meaningful name.

### 2b.1 Configuration First
Before creating a constant in source code, ask: *"Would an operator, SRE, or customer ever need to change this without redeploying?"*

If **yes** → make it **external configuration**.

| What belongs in config | Examples |
|---|---|
| Timeouts and retries | `HTTP_TIMEOUT_MS = 5000`, `MAX_RETRY_ATTEMPTS = 3` |
| Thresholds and limits | `MAX_UPLOAD_SIZE_MB = 100`, `CACHE_TTL_SECONDS = 300` |
| Feature flags | `ENABLE_NEW_CHECKOUT = true` |
| External endpoints | `PAYMENT_GATEWAY_URL = "https://..."` |
| Business rules / pricing | `VAT_RATE = 0.20`, `FREE_SHIPPING_THRESHOLD = 50.00` |
| Rate limits | `API_RATE_LIMIT_PER_MINUTE = 100` |

**Why configuration first?**
- Operations can tune values during incidents without a code change.
- Different environments (dev, staging, prod) need different values.
- A/B tests and gradual rollouts require runtime variability.
- Regulatory or regional differences (tax rates, legal limits) change independently of code releases.

**Implementation patterns:**
- Environment variables for simple key-value pairs.
- Configuration files (YAML, JSON, TOML, `.properties`) for structured settings.
- Configuration services (Consul, etcd, AWS AppConfig, Spring Cloud Config) for centralized, dynamic config.
- Type-safe configuration objects injected at startup — never scatter `System.getenv()` calls through business logic.

**Anti-pattern:**
- ❌ Hardcoding a timeout because "it never changes" — until the first production outage.

### 2b.2 Enumerations
Use an enum when the literal represents a member of a **closed set of semantic choices**.

- ❌ `if (status == 2)` — what is 2?
- ✅ `if (status == OrderStatus.SHIPPED)`

This applies to:
- Status codes (`OrderStatus`, `PaymentState`)
- Types and categories (`UserRole.ADMIN`, `LogLevel.ERROR`)
- Named flags and options

### 2b.3 Named Constants
Use a named constant when the value is **stable across all environments** and represents a fixed physical, mathematical, or protocol truth.

| What belongs as a constant | Examples |
|---|---|
| Mathematical constants | `PI = 3.1415926535`, `E = 2.71828` |
| Time conversions | `SECONDS_PER_MINUTE = 60`, `MILLIS_PER_SECOND = 1000` |
| Protocol values | `HTTP_PORT = 80`, `IPv4_ADDRESS_PARTS = 4` |
| Buffer sizes (if fixed by design) | `MAX_HEADER_SIZE = 8192` |

**Rules for constants:**
- Name the **meaning**, not the value.
  - ❌ `const int FIVE = 5;`
  - ✅ `const int WORK_DAYS_PER_WEEK = 5;`
- Keep constants close to their usage, or in a dedicated constants file if widely shared.
- Use the type system: `Duration TIMEOUT = Duration.ofSeconds(5)` is better than `int TIMEOUT_SECONDS = 5`.

### 2b.4 What to Avoid
- ❌ Raw literals scattered in expressions: `price * 1.20` (what is 1.20? VAT? Margin? Currency conversion?)
- ❌ Constants that should be configs: `const MAX_CONNECTIONS = 10` in source code when the DBA needs to raise it under load.
- ❌ "Config constants" — constants loaded from config but then re-assigned to constant fields at startup are fine; the point is that the **source of truth** must be external if the value is operational.

### 2b.5 Examples

**Before:**
```java
if (responseTime > 5000) {
    retryCount = 3;
}
```

**After (Configuration):**
```java
if (responseTime > config.getResponseTimeoutMs()) {
    retryCount = config.getMaxRetryAttempts();
}
```

**Before:**
```java
double vat = price * 0.20;
```

**After (Configuration for business rule):**
```java
 double vat = price * config.getVatRate();
```

**Before:**
```java
Thread.sleep(1000);
```

**After (Named constant for universal conversion):**
```java
Thread.sleep(MILLIS_PER_SECOND); // if truly universal
// OR better:
Thread.sleep(config.getPollingIntervalMs()); // if operational
```

---

## 3. Functions

Functions are the verbs of the language. They do the work.

### 3.1 Small!
- Functions should be **very small** (ideally under 20 lines, often under 10).
- Blocks within `if`, `else`, `while` should be a single line — usually a function call.
- Indentation should rarely exceed 1 or 2 levels.

### 3.2 Do One Thing
- A function should do **one thing only**, and do it well.
- **Test**: If you can extract another function with a meaningful name from it, it is doing more than one thing.
- **Test**: It should be impossible to divide the function into sections (e.g., "first we do X, then we do Y").

### 3.3 One Level of Abstraction per Function
- Mixing high-level business logic with low-level bit-twiddling is confusing.
- Refactor so that each function deals with concepts at one level of abstraction.

### 3.4 The Stepdown Rule
- Read code from top to bottom like a narrative.
- Each function should be followed by functions at the next level of abstraction.
- This creates a natural flow: high-level orchestration at the top, details at the bottom.

### 3.5 Switch Statements
- Avoid `switch`/`if-else` chains that violate Open/Closed Principle.
- Encapsulate them in a low-level class, and use polymorphism to hide them from higher-level code.

### 3.6 Function Arguments
- **0 arguments**: ideal (niladic)
- **1 argument**: good (monadic)
- **2 arguments**: acceptable (dyadic)
- **3 arguments**: suspicious (triadic)
- **More than 3**: requires strong justification. Consider an argument object.

**Specific argument rules:**
- **No flag arguments**: A boolean argument means the function does more than one thing. Split it.
  - ❌ `render(true)` → ✅ `renderForSuite()` and `renderForSingleTest()`
- **No output arguments**: Functions should return values, not modify passed-in arguments.
  - ❌ `appendFooter(s)` → ✅ `s.appendFooter()` or `String s = report.footer()`
- **Argument objects**: If arguments naturally go together, group them.
  - ✅ `makeCircle(Point center, double radius)` instead of `makeCircle(double x, double y, double r)`
- **Verbs and keywords**: Function names should form a coherent verb-noun pair with arguments.
  - ✅ `assertExpectedEqualsActual(expected, actual)`

### 3.7 Have No Side Effects
- A function should not modify hidden state or call unexpected services.
- If it must, the name must announce it clearly (e.g., `checkPasswordAndInitializeSession` is bad; `checkPassword` + `initializeSession` is better).

### 3.8 Command Query Separation (CQS)
- A function should either:
  - **Do something** (command: has a side effect, returns void)
  - **Answer something** (query: returns a value, has no side effects)
- Never both.
  - ❌ `boolean set(String attribute, String value)` — sets AND tells if it succeeded.
  - ✅ Separate into `void set(...)` and `boolean attributeExists(...)`.

### 3.9 Prefer Exceptions to Returning Error Codes
- Error codes force the caller to handle them immediately, cluttering the main logic.
- Exceptions separate error handling from happy-path logic.
- Extract `try/catch` blocks into their own functions.

### 3.10 Don't Repeat Yourself (DRY)
- Duplication is the root of much evil. Every duplicated line is a future inconsistency bug.
- Extract common logic into functions, classes, or templates.

---

## 4. Comments

> "Comments do not make up for bad code." — Robert C. Martin

### 4.1 Golden Rule
- The only truly good comment is the comment you found a way not to write.
- When you feel the need to write a comment, try to refactor the code so that the comment becomes unnecessary.

### 4.2 Good Comments (use sparingly)
- **Legal comments**: Copyright, licenses (keep minimal).
- **Informative comments**: Reference to an algorithm, paper, or standard.
  - ✅ `// Matches the format specified in RFC 5322`
- **Explanation of intent**: Why a non-obvious approach was chosen.
  - ✅ `// We chose insertion sort here because the list is usually very small (< 10 items)`
- **Clarification**: Translating a complex but optimized expression.
- **Warning of consequences**: Known gotchas or performance traps.
  - ✅ `// This call may take several minutes on large datasets`
- **TODO comments**: Mark future improvements, but do not let them live forever. Track them in tickets.
- **Public API documentation**: Javadoc, KDoc, docstrings for libraries.

### 4.3 Bad Comments (avoid at all costs)
- **Redundant comments**: State the obvious.
  - ❌ `i++; // increment i`
- **Mumbling**: Incomplete, unclear, or written in haste.
- **Misleading comments**: Worse than no comment. Keep them in sync or delete them.
- **Mandated comments**: Policy-driven noise (author tags, change logs in comments).
- **Journal comments**: Do not keep a change history in file headers — Git does this.
- **Noise comments**: `// Default constructor`, `// The name`
- **Closing brace comments**: `// end if`, `// end while` — if you need them, the function is too long.
- **Attributions/Bylines**: Git blame tells you who wrote what.
- **Commented-out code**: **Delete it**. It rots instantly and confuses readers. Git preserves history.
- **HTML in comments**: Hard to read in IDEs.
- **Nonlocal information**: Describing another file or system in a local comment.
- **Too much information**: Essays in comments. Put that in docs.

---

## 5. Formatting

Formatting is about communication. Code is read far more often than it is written.

### 5.1 Vertical Formatting
- **Newspaper metaphor**: The top gives the high-level concept, details increase as you read down.
- **Vertical openness**: Separate distinct concepts with blank lines.
- **Vertical density**: Group closely related lines together.
- **Vertical distance**:
  - Declare variables close to their first use.
  - Instance variables at the top of the class (or bottom, but be consistent).
  - Dependent functions should be vertically close; the caller should be above the callee.
  - Similar concepts should be grouped together.

### 5.2 Horizontal Formatting
- **Line length**: Keep lines under 80-120 characters. Long lines strain reading and diffs.
- **Spacing**:
  - Space around operators: `a + b`, not `a+b`.
  - No space between function name and opening parenthesis: `doSomething()`, not `doSomething ()`.
  - Space after commas: `doSomething(a, b, c)`.
- **Indentation**: Be consistent with the project standard. Use automated formatters.

### 5.3 Team Rules
- The code of a team should look like it was written by **a single person**.
- Agree on a style guide and enforce it automatically.
- Do not fight over style in code reviews — the formatter owns style; humans own logic.

---

## 6. Objects and Data Structures

### 6.1 Data/Object Anti-Symmetry
- **Objects** hide data and expose operations (behavior). Think: `Queue` — you `enqueue` and `dequeue`, you do not touch the internal array.
- **Data structures** expose data and have no significant behavior. Think: DTOs, JSON maps, plain records.
- **Hybrids are dangerous**: A structure with getters/setters AND business logic is neither fish nor fowl. Be deliberate.

### 6.2 The Law of Demeter
A method should only call:
1. Itself
2. Its own fields
3. Its parameters
4. Locally created objects
5. Global variables (avoid these)

- ❌ `context.getOptions().getDir().getAbsolutePath()` — train wreck
- ✅ `context.getAbsoluteDirPath()` — encapsulate the navigation

### 6.3 Data Transfer Objects (DTOs)
- Useful for raw data shuffling (APIs, databases).
- Keep them dumb. Do not add business logic to DTOs.

### 6.4 Active Record
- Pattern where a data structure also has database access methods (e.g., `save()`, `find()`).
- Treat Active Records as data structures. Put business logic in separate domain objects.

---

## 7. Error Handling

Error handling is important, but it should not obscure the main logic.

### 7.1 Use Exceptions, Not Return Codes
- Return codes force immediate handling and clutter the caller.
- Exceptions allow the happy path to remain clean.

### 7.2 Write Try-Catch-First
- When writing code that may throw, start with the `try-catch-finally` structure to define the scope and expected behavior.

### 7.3 Unchecked Exceptions
- Prefer unchecked (runtime) exceptions over checked exceptions in most modern languages.
- Checked exceptions violate Open/Closed Principle by forcing all callers up the stack to know about low-level errors.

### 7.4 Provide Context
- Exception messages should tell **what** failed and **why**, including relevant variable values.
  - ❌ `throw new Exception("Error");`
  - ✅ `throw new Exception("Failed to parse user profile for ID: " + userId);`

### 7.5 Define Exception Classes by Caller Need
- Create exception hierarchies that let callers catch what they care about.
  - ✅ `DeviceCommException` rather than exposing `PortInUseException`, `SocketTimeoutException`, etc.

### 7.6 Null Is a Billion-Dollar Mistake
- **Do not return null**. Return empty collections, Optional/Maybe types, or Null Objects.
- **Do not pass null** as an argument. Use method overloading, default parameters, or separate methods.

---

## 8. Classes

### 8.1 Organization
- Standard order (Java/Kotlin/C# style):
  1. Public static constants
  2. Private static variables
  3. Private instance variables
  4. Public functions
  5. Private utility functions (called by public ones)
- Keep everything **private** by default. Relax encapsulation deliberately, not by default.

### 8.2 Small Classes
- Like functions, classes should be small — measured in **responsibilities**, not lines.
- **Single Responsibility Principle (SRP)**: A class should have one, and only one, reason to change.
- If you cannot write a concise description of the class without using words like "and" or "or," it likely has too many responsibilities.

### 8.3 Cohesion
- A class is cohesive when most of its methods use most of its instance variables.
- High cohesion = small, focused classes.
- If a class loses cohesion, split it.

### 8.4 Isolating Change
- **Open/Closed Principle (OCP)**: Open for extension, closed for modification.
  - New behavior should be added by writing new code, not changing old code.
- **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concrete details.
  - High-level modules should not depend on low-level modules. Both should depend on abstractions.

### 8.5 Class Design Smells
- **God Class**: A class that knows too much or does too much.
- **Feature Envy**: A method that seems more interested in another class than its own. Move it.
- **Data Clumps**: Groups of variables that travel together everywhere. Make them a class.
- **Primitive Obsession**: Using primitives instead of small objects for domain concepts.
  - ❌ `String phoneNumber`
  - ✅ `PhoneNumber phoneNumber`

---

## 9. Unit Tests

Tests are as important as production code. They are the safety net and the documentation.

### 9.1 The Three Laws of TDD
1. You may not write production code until you have written a failing unit test.
2. You may not write more of a unit test than is sufficient to fail (compilation failures count).
3. You may not write more production code than is sufficient to pass the currently failing test.

Even if you do not practice strict TDD, treat tests with the same respect as production code.

### 9.2 Keep Tests Clean
- Dirty tests are worse than no tests — they become a maintenance burden and are abandoned.
- Tests must **change as the production code evolves**. Make them easy to modify.

### 9.3 One Concept per Test
- A test should verify one logical concept.
- If a test has multiple assertions, ensure they are checking different aspects of the same concept.
- Better: split into multiple well-named tests.

### 9.4 F.I.R.S.T. Principles
| Letter | Meaning | Guidance |
|---|---|---|
| **F**ast | Tests should run quickly. | Slow tests are not run often. Avoid real databases, networks, or sleeps in unit tests. Mock external dependencies. |
| **I**ndependent | Tests should not depend on each other. | Each test sets up its own state. No shared mutable state between tests. Run them in any order. |
| **R**epeatable | Tests should be reproducible in any environment. | No randomness, no hardcoded paths, no environmental assumptions. They must pass on your machine, CI, and a colleague's laptop. |
| **S**elf-Validating | Tests must have a boolean outcome. | Pass or fail. No manual inspection of logs or output files. Use assertions, not `System.out.println`. |
| **T**imely | Tests should be written at the right time. | Ideally before the production code (TDD). At worst, immediately after. Never "later." |

### 9.5 Given-When-Then / Arrange-Act-Assert
Structure every test clearly:
```
// Given — setup the state
// When — execute the action under test
// Then — verify the outcome
```

### 9.6 Test Naming
- Names should be descriptive enough that you know what failed without reading the code.
- Use full sentences describing the behavior.
  - ❌ `testUser()`
  - ✅ `shouldThrowExceptionWhenEmailIsInvalid()`
  - ✅ `deactivatesAccountWhenPaymentFailsThreeTimes()`

### 9.7 Test Data
- Use meaningful values, even in tests. Avoid `foo`, `bar`, `12345` unless irrelevant.
- Use builders or factory methods to reduce test setup noise.

### 9.8 Coverage
- Aim for high coverage, but do not worship the metric.
- 100% coverage with bad assertions is meaningless.
- Focus on **behavior coverage** (paths, boundaries, error cases), not just line coverage.

### 9.9 Test Doubles
- **Mocks**: Verify interactions (use sparingly; they couple tests to implementation).
- **Stubs**: Provide canned answers.
- **Fakes**: Working lightweight implementations (e.g., in-memory database).
- **Prefer fakes over mocks** when possible — they are more robust to refactoring.

### 9.10 Integration & E2E Tests
- Unit tests are the foundation, but not sufficient.
- Add integration tests for boundaries (DB, HTTP, message queues).
- Add E2E tests for critical user journeys.
- Follow the Test Pyramid: many unit tests, fewer integration tests, very few E2E tests.

---

## 10. Code Smells & Heuristics (Review Checklist)

Use this checklist during every code review or refactoring session.

### C — Comments
- [ ] **C1**: No inappropriate information (author tags, change logs in comments)
- [ ] **C2**: No obsolete comments
- [ ] **C3**: No redundant comments
- [ ] **C4**: Comments are well-written and necessary
- [ ] **C5**: No commented-out code

### E — Environment
- [ ] **E1**: Build requires only one step
- [ ] **E2**: Tests require only one step

### F — Functions
- [ ] **F1**: Few arguments (0-2 ideal, 3 maximum)
- [ ] **F2**: No output arguments
- [ ] **F3**: No flag arguments (boolean switches)
- [ ] **F4**: No dead (unreachable/uncalled) functions

### G — General
- [ ] **G1**: One language per source file
- [ ] **G2**: Obvious behavior is implemented
- [ ] **G3**: Correct behavior at boundaries (edge cases handled)
- [ ] **G4**: No overridden safeties
- [ ] **G5**: **No duplication** (DRY)
- [ ] **G6**: Code at the right level of abstraction
- [ ] **G7**: Base classes do not depend on derivatives
- [ ] **G8**: Not too much information exposed (strong encapsulation)
- [ ] **G9**: No dead code
- [ ] **G10**: Vertical separation — variables declared close to use
- [ ] **G11**: Consistency in naming and style
- [ ] **G12**: No clutter (unused imports, unused variables)
- [ ] **G13**: No artificial coupling (unrelated things bound together)
- [ ] **G14**: No Feature Envy (method belongs in another class)
- [ ] **G15**: No selector arguments (mode/type parameters driving behavior)
- [ ] **G16**: Intent is not obscured
- [ ] **G17**: Responsibilities are appropriately placed
- [ ] **G18**: No inappropriate static methods
- [ ] **G19**: Explanatory variables for complex expressions
- [ ] **G20**: Function names say what they do
- [ ] **G21**: Understand the algorithm before coding it
- [ ] **G22**: Make logical dependencies physical (e.g., do not assume array ordering without asserting it)
- [ ] **G23**: Prefer polymorphism to if/else or switch/case
- [ ] **G24**: Follow standard conventions
- [ ] **G25**: Replace magic numbers — **configuration first**, then enumerations, then named constants
- [ ] **G26**: Be precise (right types, right scopes, right visibility)
- [ ] **G27**: Structure over convention (enforce rules in code, not culture)
- [ ] **G28**: Encapsulate conditionals (`if (shouldBeDeleted(timer))` not `if (timer.hasExpired() && !timer.isRecurrent())`)
- [ ] **G29**: Avoid negative conditionals (`if (isActive)` not `if (!isInactive)`)
- [ ] **G30**: Functions do one thing
- [ ] **G31**: No hidden temporal couplings
- [ ] **G32**: Do not be arbitrary (if there is a rule, follow it everywhere)
- [ ] **G33**: Encapsulate boundary conditions
- [ ] **G34**: Functions descend only one level of abstraction
- [ ] **G35**: Keep configurable data at high levels (see Section 2b: Magic Numbers & Configuration)
- [ ] **G36**: Avoid transitive navigation (Law of Demeter)

### J — Java (adapt to your language)
- [ ] **J1**: Avoid long import lists with wildcards
- [ ] **J2**: Do not inherit constants
- [ ] **J3**: Prefer enums over constants

### N — Names
- [ ] **N1**: Choose descriptive names
- [ ] **N2**: Choose names at the appropriate level of abstraction
- [ ] **N3**: Use standard nomenclature where possible
- [ ] **N4**: Unambiguous names
- [ ] **N5**: Long names for long scopes, short names for short scopes
- [ ] **N6**: Avoid encodings
- [ ] **N7**: Names should describe side-effects

### T — Tests
- [ ] **T1**: Sufficient tests (meaningful coverage)
- [ ] **T2**: Use a coverage tool
- [ ] **T3**: Do not skip trivial tests
- [ ] **T4**: An ignored (`@Ignore`, `@Skip`) test is a question about ambiguity
- [ ] **T5**: Test boundary conditions
- [ ] **T6**: Exhaustively test near bugs (regression tests)
- [ ] **T7**: Patterns of failure are revealing
- [ ] **T8**: Test coverage patterns can reveal untested code paths
- [ ] **T9**: Tests should be fast

---

## 11. SOLID Principles (Design Reference)

| Principle | Definition | Practical Guide |
|---|---|---|
| **S**ingle Responsibility | A module should have one reason to change. | If you describe a class and say "and" or "or," split it. |
| **O**pen/Closed | Open for extension, closed for modification. | Use interfaces, abstract classes, and composition. |
| **L**iskov Substitution | Subtypes must be substitutable for their base types. | A `Square` is not a `Rectangle` in code. Respect contracts. |
| **I**nterface Segregation | Clients should not depend on methods they do not use. | Prefer many small interfaces over one fat interface. |
| **D**ependency Inversion | Depend on abstractions, not concretions. | Inject dependencies. Do not `new` inside business logic. |

---

## 12. How to Apply This Skill (Workflow)

### When starting a new feature
1. Write the test first (TDD) or at least define the contract.
2. Choose names that reveal intent before implementing.
3. Write the smallest function that passes the test.
4. Refactor immediately: extract, rename, remove duplication.

### When refactoring
1. Run the tests — they must be green before you start.
2. Make one small change at a time.
3. Run the tests after every change.
4. Stop when the code is cleaner, not when it is perfect.

### During code review
1. Read the code top-to-bottom. Does it tell a story?
2. Run the checklist (C, E, F, G, N, T).
3. Ask: "Is this the simplest way to express the intent?"
4. Suggest renames and extractions before algorithmic changes.

### When debugging
1. Look for smells first: duplication, long functions, side effects.
2. Bugs love to hide in messy code. Cleaning often reveals the bug.

---

> *"The only way to go fast is to go well."*
