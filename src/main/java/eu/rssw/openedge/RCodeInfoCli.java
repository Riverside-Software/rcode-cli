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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import eu.rssw.pct.RCodeInfo;
import eu.rssw.pct.RCodeInfo.InvalidRCodeException;

public class RCodeInfoCli {

  public static void main(String[] args) throws IOException, InvalidRCodeException {
    try (InputStream input = new FileInputStream(args[0])) {
      RCodeInfo rci = new RCodeInfo(input);
      System.out.println("Version: " + (rci.getVersion() & 0x3FFF) + (rci.is64bits() ? " 64 bits" : ""));
      System.out.println("Timestamp: " + rci.getTimeStamp());
      System.out.println("CRC: " + rci.getCrc());
      if (rci.getDigest() != null)
        System.out.println("Digest: " + rci.getDigest());
    }
  }
}
