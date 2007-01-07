/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.shouter;

import games.stendhal.client.update.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.ariannexpTimeoutException;
import marauroa.client.net.DefaultPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.MessageS2CPerception;
import marauroa.common.net.TransferContent;

/**
 * Connects to the server and shouts a message.
 *
 * @author hendrik
 */
public class ShouterMain {
	private String host;
	private String username;
	private String password;
	protected String character;
    private String port;
    private boolean tcp;
    protected long lastPerceptionTimestamp = 0;
	protected Map<RPObject.ID, RPObject> world_objects;
	protected marauroa.client.ariannexp clientManager;
	protected PerceptionHandler handler;

	/**
	 * Creates a PostmanMain
	 *
	 * @param h host
	 * @param u username
	 * @param p password
	 * @param c character name
	 * @param P port
	 * @param t TCP?
	 * @throws SocketException on an network error
	 */
	public ShouterMain(String h, String u, String p, String c, String P, boolean t)
			throws SocketException {
		host = h;
		username = u;
		password = p;
		character = c;
        port = P;
        tcp = t;

		world_objects = new HashMap<RPObject.ID, RPObject>();

		handler = new PerceptionHandler(new DefaultPerceptionListener() {
			@Override
			public int onException(Exception e,
					marauroa.common.net.MessageS2CPerception perception) {
				System.out.println(perception);
				System.err.println(perception);
				e.printStackTrace();
				return 0;
			}

		});

		clientManager = new marauroa.client.ariannexp(
				"games/stendhal/log4j.properties") {
			@Override
			protected String getGameName() {
				return "stendhal";
			}

			@Override
			protected String getVersionNumber() {
				return Version.VERSION;
			}

			@Override
			protected void onPerception(MessageS2CPerception message) {
				lastPerceptionTimestamp = System.currentTimeMillis();
				try {
					handler.apply(message, world_objects);
				} catch (Exception e) {
					onError(3, "Exception while applying perception");
				}
			}

			@Override
			protected List<TransferContent> onTransferREQ(
					List<TransferContent> items) {
				for (TransferContent item : items) {
					item.ack = true;
				}

				return items;
			}

			@Override
			protected void onServerInfo(String[] info) {
				// do nothing
			}

			@Override
			protected void onError(int code, String reason) {
				System.out.println(reason);
				System.err.println(reason);
				Runtime.getRuntime().halt(1);
			}

			@Override
			protected void onAvailableCharacters(String[] characters) {
				try {
					chooseCharacter(character);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onTransfer(List<TransferContent> items) {
				// do nothing
			}
		};
	}

	public void script() {
		try {
			clientManager.connect(host, Integer.parseInt(port), tcp);
			clientManager.login(username, password);
	        readMessagesAndShoutThem();
	        clientManager.logout();
	        System.exit(0);

	        // exit with an exit code of 1 on error
		} catch (SocketException e) {
			System.err.println("Socket Exception");
			Runtime.getRuntime().halt(1);
		} catch (ariannexpTimeoutException e) {
			System.err.println("Cannot connect to Stendhal server. Server is down?");
			Runtime.getRuntime().halt(1);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace(System.err);
			Runtime.getRuntime().halt(1);
		}

	}
	
	private void readMessagesAndShoutThem() throws IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		while (line != null) {
			if (line.trim().length() > 0) {
				shout(line);
			}
	        Thread.sleep(1000);
		}
		br.close();
	}

    private void shout(String message) {
        RPAction chat=new RPAction();
        chat.put("type","tellall");
        chat.put("text", message);
        clientManager.send(chat);
    }

	/**
	 * Main entry point
	 *
	 * @param args see help
	 */
	public static void main(String[] args) {
		try {
			if (args.length > 0) {
				int i = 0;
				String username = null;
				String password = null;
				String character = null;
				String host = null;
                String port = null;
                boolean tcp = false;

				while (i != args.length) {
					if (args[i].equals("-u")) {
						username = args[i + 1];
					} else if (args[i].equals("-p")) {
						password = args[i + 1];
					} else if (args[i].equals("-c")) {
						character = args[i + 1];
					} else if (args[i].equals("-h")) {
						host = args[i + 1];
                     } else if (args[i].equals("-P")) {
                         port = args[i + 1];
                     } else if (args[i].equals("-t")) {
                    	 tcp = true;
                     }
					i++;
				}

				if (username != null && password != null && character != null
						&& host != null && port != null) {
					ShouterMain shouter = new ShouterMain(host, username, password, character, port, tcp);
					shouter.script();
					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.bot.shouter.Shouter -u username -p pass -h host -P port -c character");
			System.out.println();
			System.out.println("Required parameters");
			System.out.println("* -h\tHost that is running Marauroa server");
			System.out.println("* -P\tPort on which Marauroa server is running");
			System.out.println("* -u\tUsername to log into Marauroa server");
			System.out.println("* -p\tPassword to log into Marauroa server");
			System.out.println("* -c\tCharacter used to log into Marauroa server");
			System.out.println("Optional parameters");
			System.out.println("* -t\tuse tcp-connection to server");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
