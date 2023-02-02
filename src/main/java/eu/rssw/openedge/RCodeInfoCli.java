/*
 * OpenEdge plugin for SonarQube
 * Copyright (c) 2015-2023 Riverside Software
 * contact AT riverside DASH software DOT fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package eu.rssw.openedge;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import eu.rssw.pct.RCodeInfo;
import eu.rssw.pct.RCodeInfo.InvalidRCodeException;

public class RCodeInfoCli {
  private static CompareCommand compare = new CompareCommand();
  private static ScanCommand scan = new ScanCommand();

  private PrintStream out;
  private PrintStream err;

  public RCodeInfoCli() {
    this(System.out, System.err);
  }

  public RCodeInfoCli(PrintStream out, PrintStream err) {
    this.out = out;
    this.err = err;
  }

  public static void main(String[] args) throws IOException, InvalidRCodeException {
    RCodeInfoCli main = new RCodeInfoCli();
    JCommander jc = new JCommander(main);
    jc.addCommand("compare", compare);
    jc.addCommand("scan", scan);

    try {
      jc.parse(args);
    } catch (ParameterException caught) {
      jc.usage();
      System.exit(1);
    }
    try {
      if ("compare".equals(jc.getParsedCommand()))
        main.executeCompare();
      else if ("scan".equals(jc.getParsedCommand()))
        main.executeScan();

    } catch (IOException caught) {
      main.out.println("I/O problem: " + caught.getMessage());
      System.exit(1);
    }

  }

  private void executeScan() throws IOException {
    FileCollector fc1 = new FileCollector(scan.libs.get(0));
    Files.walkFileTree(fc1.initPath, fc1);

    out.printf("%6s %44s %s%n", "CRC", "Digest", "File name");
    fc1.files.stream().forEach(it -> {
      try (InputStream s1 = Files.newInputStream(fc1.initPath.resolve(it))) {
        RCodeInfo rci1 = new RCodeInfo(s1);
        out.printf("%6s %44s %s%n", rci1.getCrc(), rci1.getDigest(), it);
      } catch (IOException | InvalidRCodeException caught) {
        err.printf("Unable to read %s%n" + it);
        caught.printStackTrace(err);
      }
    });
  }

  private void executeCompare() throws IOException {
    FileCollector fc1 = new FileCollector(compare.libs.get(0));
    FileCollector fc2 = new FileCollector(compare.libs.get(1));
    Files.walkFileTree(fc1.initPath, fc1);
    Files.walkFileTree(fc2.initPath, fc2);

    fc1.files.stream().forEach(it -> {
      if (!fc2.files.contains(it)) {
        out.printf("%s%n", it);
      } else {
        try (InputStream s1 = Files.newInputStream(compare.libs.get(0).resolve(it));
            InputStream s2 = Files.newInputStream(compare.libs.get(1).resolve(it))) {
          RCodeInfo rci1 = new RCodeInfo(s1);
          RCodeInfo rci2 = new RCodeInfo(s2);
          if ((rci1.getCrc() != rci2.getCrc()) || !rci1.getDigest().equals(rci2.getDigest())) {
            out.printf("%s%n", it);
          }
        } catch (IOException | InvalidRCodeException caught) {
          err.printf("Unable to read %s%n" + it);
          caught.printStackTrace(err);
        }
      }
    });
  }

  private static class FileCollector implements FileVisitor<Path> {
    private final List<String> files = new ArrayList<>();
    private final Path initPath;

    public FileCollector(Path initPath) {
      this.initPath = initPath;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (file.getFileName().toString().endsWith(".r"))
        files.add(initPath.relativize(file).toString());
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }
  }

  @Parameters(commandDescription = "Compare two rcode directories")
  public static class CompareCommand {
    @Parameter(arity = 2, description = "sourceDir targetDir", required = true)
    private List<Path> libs;
  }

  @Parameters(commandDescription = "Scan rcode directory")
  public static class ScanCommand {
    @Parameter(arity = 1, description = "sourceDir", required = true)
    private List<Path> libs;
  }

}
