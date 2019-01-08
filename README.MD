# The CECOTool transformer
This tool works uses the recommendation output of the CECOTool Analyzer to apply the recommendations to a target application.

# Usage
Use java -jar cecotool-transformer.jar -h to read the following:
```
Usage: <main class> [-h] -f=<recommendationFile> -t=<targetProjectDirectory>
  -f, --file=<recommendationFile>
               The recommendations file
  -h, --help   Displays this help
  -t, --target=<targetProjectDirectory>
               The target project base classes directory, root of the Java classes
```
Example of the target directory: <YOUR_PROJECT_DIRECTORY>/src/main/java

# Tips
Use git on your target project so you can easily roll back the recommendations, modify the recommendation file and try again.

# Releases
Refer to the releases section of github