/*
 * EPUB Generator
 * Copyright (C) 2022 qwq233 qwq233@qwq2333.top
 * https://github.com/qwq233/epub-generator
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by qwq233.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/qwq233/qwq233/blob/master/eula.md>.
 */

package top.qwq2333.util;

public class Console {
    private int currentStatus = 0;
    private String buffer = null;
    private final int maxStatus = 100;
    private String TAG = "Unset";
    private boolean wait = false;

    private final static Console Instance = new Console();

    public void initProgressBar() {
        while (true) {
            if (buffer != null) {
                print("\r"); // Overwrite the previous output of the progress bar
                print(TAG + ": " + buffer + "\r\n");
                buffer = null; // Clear buffer
                print("\r");
                wait = false;
            }

            if (wait)
                continue;

            print("Current Status: " + currentStatus + "/" + maxStatus + "\r");
        }
    }

    public static void init() {
        Instance.initProgressBar();
    }

    private static void print(String msg) {
        System.out.print(msg);
    }

    /**
     * Set tag <br/>
     * <p>
     * When you print a message. it will show at the beginning of the line <br/>
     * <p>
     * target tag cannot be "" <br/>
     *
     * @param tag Tag you want to set
     * @throws NullPointerException if target tag is "" then throw NPE
     */
    public static void setTag(String tag) {
        Instance.TAG = tag;
    }

    /**
     * print a message
     *
     * @param msg message
     */
    public static void printMsg(String msg) throws InterruptedException {
        synchronized (Instance) {
            if (Instance.wait) {
                Thread.sleep(100);
                printMsg(msg);
            } else {
                Instance.wait = true;
                Instance.buffer = msg;
            }
        }
    }

    public static void spaceLine() throws InterruptedException {
        synchronized (Instance) {
            if (Instance.wait) {
                Thread.sleep(100);
                printMsg("\n");
            } else {
                Instance.wait = true;
                Instance.buffer = "\n";
            }
        }
    }

    /**
     * Increase progress bar progress
     *
     * @param number progress bar increment value
     */
    public static void addProgress(int number) {
        final int target = Instance.currentStatus + number;
        if (target >= Instance.maxStatus) {
            Instance.currentStatus = Instance.maxStatus;
        } else {
            Instance.currentStatus += target;
        }
    }

}
