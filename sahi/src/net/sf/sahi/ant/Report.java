package net.sf.sahi.ant;

/**
 * Sahi - Web Automation and Test Tool
 * 
 * Copyright  2006  V Narayan Raman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * User: dlewis
 * Date: Dec 5, 2006
 * Time: 6:23:55 PM
 */
public class Report {

    private String logDir;
    private String type;

    public Report() {
        super();
    }

    public Report(String type, String logDir) {
        this.logDir = logDir;
        this.type = type;
    }

    public String getLogDir() {
        return logDir;
    }

    public String getType() {
        return type;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public void setType(String type) {
        this.type = type;
    }
}
