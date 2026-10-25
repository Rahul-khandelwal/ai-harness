package com.rahul.ai.harness;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

@Service
public class DockerTools {

  @Tool("Fetches the last 20 lines of logs from a Docker container")
  public String getContainerLogs(@P("name of the container") String containerName) {
    System.out.printf("TOOL CALLED: getContainerLogs({%s})%n", containerName);
    return runCommand("docker", "logs", "--tail", "20", containerName);
  }

  @Tool("Executes a Linux repair command inside a Docker container")
  public String execContainerRepair(@P("name of the container") String containerName,
                                    @P("Linux repair command") String command) {
    System.out.printf("TOOL CALLED: execContainerRepair({%s}, {%s})%n", containerName, command);

    // HARNESS GUARDRAIL: Only allow deleting .lock files in /tmp/
    if (!command.matches("^rm (-f )?/tmp/[a-zA-Z0-9._-]+\\.lock$")) {
      System.out.printf("GUARDRAIL BLOCKED: {%s}%n", command);
      return "ERROR: Command blocked by security policy. Use exact format: 'rm -f /tmp/<filename>.lock' (no wildcards or chaining).";
    }

    System.out.println("GUARDRAIL ALLOWED: Executing repair command");
    return runCommand("docker", "exec", containerName, "sh", "-c", command);
  }

  private String runCommand(String... command) {
    try {
      Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
      String output = new String(process.getInputStream().readAllBytes());
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        System.out.printf("Command failed (exit {%d}): {%s}%n", exitCode, output);
        return "Command failed with exit code " + exitCode + ": " + output;
      }
      return output.isBlank() ? "Command executed successfully (exit code 0)." : output;
    } catch (Exception e) {
      return "Error executing command: " + e.getMessage();
    }
  }
}
