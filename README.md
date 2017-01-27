DEPRECATED!!! All things are moved to [another repo](https://github.com/jvaluer/jvaluer)

JValuer [![](https://jitpack.io/v/petuhovskiy/JValuer.svg)](https://jitpack.io/#petuhovskiy/JValuer)
=======
Java lib for solutions testing. Requires Java 8.

## Usage
### Create JValuer
```java
JValuer jValuer = new JValuerBuilder()
  .addLanguage(new Language("GNU C++11", 
    new RunnableCompiler("g++", "{defines} -O2 -o {output} {source}")), //pattern of compiler
    new String[]{"cpp"}, 
    new String[]{"c++11", "cpp11", "cpp"})
  .addLanguage(new Language("Python 3", 
    new CloneCompiler(),  //compiler that just copy .py file
    new CustomInvoker(new OSRelatedValue<String>() //model of executing program
      .windows("c:/Programs/Python-3/python.exe")
      .orElse("python3"), 
      "{exe} {args}")),
    new String[]{"py"}, 
    new String[]{"python", "py", "python3"})
  .setPath(Paths.get("/jvaluer/tmp/")) //
  .build();
```
### Get languages
```java
Languages languages = jValuer.languages();
languages.findByExtension("py").name(); //Python 3
languages.findByName("c++11").name(); //GNU C++11
```
### Compile something
```java
Path source = Paths.get("hello_world.cpp");
CompilationResult compilation = jValuer.compile(source); //automatic language detection from path
compilation.isSuccess(); //compilation ended successfully?
compilation.getComment(); //get all stderr/stdout compiler output
Path exe = compilation.getExe(); //get executable (result of compiling) stored in JValuer temp folder
```
### Run Limits
```java
RunLimits.ofMemory(1024L * 1024L * 128L); //returns RunLimits with 128M (1024 * 1024 * 128 bytes) memory limit
RunLimits.ofTime(1000L); //1 second time limit
RunLimits.unlimited(); // no memory and time restrictions
new RunLimits(1000L, 1024L * 1024L * 128L); //1 second, 128 Mbytes
```
### In and Out
```java
new RunInOut("sausages.in", "sausages.out"); //represents sausages.(in/out) as input and output
RunInOut.txt(); //input.txt / output.txt
RunInOut.std(); //stdin / stdout. Use Standart In Stream and Standard Out Stream for process
```
### RunnerBuilder
```java
RunnerBuilder builder = ... //builder obtained from jValuer or anywhere else
  builder
    .limits(RunLimits.ofTime(1000L) //set limits. unlimited by default
    .inOut(RunInOut.std()) //set in/out. std by default
    .trusted(true) // run process without security restrictions, in "trusted" mode. Default mode is "untrusted"
    .invoker(new DefaultInvoker()) //set custom invoker. DefaultInvoker by default
    .injectDll(Paths.get("wow.dll")) //inject dll
    .account(new UserAccount("password", "login")) //run process with specified account
    .args("1 2 3") //AFAIR arguments used to run process
    .build(); //create Runner. see below
```
### Run
```java
try(Runner runner = new RunnerBuilder(jValuer)
    .trusted()
    .limits(RunLimits.ofTime(1000L))
    .build(exe)) {
  InvocationResult result = runner.run(new StringData("123 234")); //result of running
  PathData out = result.getOut(); //out
  System.out.println(out.getString()); //print out
  RunInfo run = result.getRun(); //run info, time/memory usage, exitcode
  run.getRunVerdict(); //RunVerdict.SUCCESS, RunVerdict.TIME_LIMIT_EXCEEDED,...
} //close runner, delete resources
```
### Other
```java
jValuer.cleanTemp(); //clean temp folder, delete all trash, compilation 
```
