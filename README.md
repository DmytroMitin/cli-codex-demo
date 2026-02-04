# test012026

## Usage

Run the app with sbt:

```bash
sbt run
```

Provide a message via a CLI argument:

```bash
sbt 'run "Hello from Scala"'
```

Or run the compiled class directly (after `sbt compile`) by including the Scala library and runtime dependencies on the classpath:

```bash
java -cp "target/scala-2.13/classes:$HOME/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.13.18.jar:$HOME/.ivy2/cache/com.typesafe/config/jars/config-1.4.3.jar" Main "Hello from Scala"
```

## Configuration

The message is selected by this precedence (highest to lowest):

1. CLI argument (first arg)
2. JVM property `hello.message`
3. Environment variable `HELLO_MESSAGE`
4. Typesafe config key `hello.message`
5. Standard input (first non-empty line)
6. Default message (`Hello, world!`)

Examples:

```bash
sbt 'run "From arg"'
```

```bash
sbt -Dhello.message="From JVM" run
```

```bash
HELLO_MESSAGE="From env" sbt run
```

```bash
cat > application.conf <<'EOF'
hello.message = "From config"
EOF

sbt run
```

```bash
sbt run
```

Then type `From stdin` and press Enter.
