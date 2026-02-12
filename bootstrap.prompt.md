# Bootstrap Prompt: Recreate `cli-codex-demo` From Scratch

You are bootstrapping a small Scala CLI project. Recreate the repository from zero using the requirements below.

## Objective
Build a minimal command-line app that prints a message chosen from multiple input sources with strict precedence, plus tests and CI.

## Project Profile
- Name: `cli-codex-demo`
- Build tool: sbt
- Scala version: `2.13.18`
- sbt version: `1.11.7`
- CI Java version: `11` (Temurin)

## Required Dependencies
- Runtime:
  - Typesafe Config `com.typesafe:config:1.4.3`
- Test:
  - MUnit `org.scalameta::munit:1.2.2`
  - os-lib `com.lihaoyi::os-lib:0.11.8`

## Required Repository Layout
Create and track these files:
- `.github/workflows/ci.yml`
- `.gitignore`
- `README.md`
- `build.sbt`
- `project/build.properties`
- `src/main/resources/application.conf`
- `src/main/scala/Main.scala`
- `src/test/scala/MainSuite.scala`

Do not add package declarations; keep sources in the default package.

## Application Behavior
The app should print exactly one line: the resolved message.

Use this precedence (highest to lowest):
1. First CLI argument
2. JVM property `hello.message`
3. Environment variable `HELLO_MESSAGE`
4. Typesafe config key `hello.message`
5. First non-empty line from stdin
6. Fallback default: `Hello, world!`

Behavior notes:
- Treat missing values as absent.
- Treat empty strings from config/stdin as absent.
- Only consult config when CLI arg, JVM property, and env var are all absent.
- Handle config parsing/path issues safely (do not crash; continue resolution).

## File-by-File Expectations

### `build.sbt`
- Set Scala version to `2.13.18`.
- Set project name to `cli-codex-demo`.
- Add dependencies listed above with correct scopes.
- Configure MUnit as test framework.

### `project/build.properties`
- Pin sbt to `1.11.7`.

### `src/main/resources/application.conf`
- Include a commented example for `hello.message`.
- Keep default behavior unchanged when no config value is enabled.

### `src/main/scala/Main.scala`
- Implement the precedence rules exactly.
- Read stdin only when all higher-precedence sources are absent.
- Print only the final resolved message.

### `src/test/scala/MainSuite.scala`
- Use MUnit.
- Test each precedence source independently:
  - CLI argument
  - JVM property
  - Environment variable
  - Config file value
  - stdin input
- Run the compiled main class in a subprocess (not just calling methods directly).
- Build subprocess classpath robustly for Scala library and config jar resolution.
- Assert exit code is zero and output is correct.

### `README.md`
Document:
- how to run (`sbt run`)
- how to pass CLI argument
- how to run with JVM property
- how to run with env var
- how to provide config value
- stdin usage
- precedence order

### `.github/workflows/ci.yml`
- Trigger on all push branches and pull requests.
- Use:
  - `actions/checkout@v4`
  - `actions/setup-java@v4` with Temurin 11 and sbt cache
  - `sbt/setup-sbt@v1`
- Run `sbt test`.

### `.gitignore`
Include ignores for:
- sbt/Scala build outputs (`target`, `project/target`, etc.)
- common Scala tooling dirs (`.metals`, `.bloop`, `.bsp`, `.scala-build`)
- IDE dirs (`.idea`, `.vscode`)
- local test/cache dirs used by this repo
- generic noise (`*.class`, `*.log`, `.DS_Store`)

## Acceptance Criteria
- `sbt test` passes.
- `sbt run` with no input prints `Hello, world!`.
- Precedence order is implemented exactly as listed.
- CI workflow executes tests on push and pull_request.
- No extra tracked source/config files beyond the required layout.

## Implementation Constraints
- Keep implementation minimal and readable.
- Do not introduce additional frameworks/plugins.
- Prefer deterministic behavior over convenience shortcuts.
- Ensure behavior matches README examples.
