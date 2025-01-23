## Description

Command line utility to display rcode information, and to compare two directories with rcode.

## Requirements

Java 11

## Scan command line

```
java -jar rcode-cli.jar scan /path/to/dir
```

Sample output:
```
   CRC            Timestamp                                       Digest File name
 13701 2023-02-02T09:54:08Z hSHCHhDPhk/3fWfeeeRewGbZi5kXHM+y7tbuqe+p/Y4= pct\compile.r
 36002 2023-02-02T09:54:08Z UR44rlrNd1+yyGg8skstvoQf6Hsi5vJ9gJuI7zhAPlc= pct\dmpSch.r
 56703 2023-02-02T09:54:08Z Xr9IjGTe6Zx57nhRGo2NVSCgkQNC0qioKsvmsC3Zvz8= pct\dump_seq.r
 36350 2023-02-02T09:54:08Z u08h/C8V9CeZNfu3wii2aYrgnOdv7kVGJJ63hOaKBf8= pct\dynrun.r
 ...
```

## Compare command line

```
java -jar rcode-cli.jar compare /path/to/dir1 /path/to/dir2
```

Output only shows file present in first directory and not in the second directory, as well as rcodes that have a different CRC or digest.
Output has one file per line, so it's easy to pipe the input to another command.
