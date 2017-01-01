/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.mechanicalcat.pycode;

public class Reference {
    public static final String MODID = "pycode";
    public static final String MODNAME = "Python Code";
    public static final String VERSION = "@VERSION@";
    public static final String CLIENT_PROXY_CLASS = "net.mechanicalcat.pycode.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "net.mechanicalcat.pycode.proxy.ServerProxy";
    public static final String ACCEPTED_VERSIONS = "[1.10.2]";

    public static enum PyCodeRegistrations {
        WAND("python_wand", "PythonWandItem"),
        BOOK("python_book", "PythonBookItem"),
        BLOCK("python_block", "PythonBlock"),
        HAND("python_hand", "HandItem");

        private String unlocalizedName;
        private String registryName;

        PyCodeRegistrations(String unlocalizedName, String registryName) {
            this.unlocalizedName = unlocalizedName;
            this.registryName = registryName;
        }

        public String getRegistryName() {
            return registryName;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }
    }
}
