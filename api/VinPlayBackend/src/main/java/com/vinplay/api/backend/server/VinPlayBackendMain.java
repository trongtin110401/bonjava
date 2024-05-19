/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseController
 *  com.vinplay.vbee.common.cp.NoCommandRegistered
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastLoader
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PropertyConfigurator
 *  org.eclipse.jetty.server.Handler
 *  org.eclipse.jetty.server.Server
 *  org.eclipse.jetty.servlet.FilterHolder
 *  org.eclipse.jetty.servlet.ServletHandler
 *  org.eclipse.jetty.servlet.ServletHolder
 */
package com.vinplay.api.backend.server;

import com.vinplay.api.backend.agent.utils.AgentUtils;
import com.vinplay.api.backend.gamebai.utils.GameBaiUtils;
import com.vinplay.api.backend.processors.commission.UpdateCommissionUtils;
import com.vinplay.api.backend.processors.rechargeByCardPending.CardOzzePendingUtils;
import com.vinplay.api.backend.processors.userMission.ResetUserMissionUtils;
import com.vinplay.api.backend.report.utils.BackendUtils;
import com.vinplay.api.backend.report.utils.ReportMoneyUtils;
import com.vinplay.api.backend.server.CorsFilter;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.cp.BaseController;
import com.vinplay.vbee.common.cp.NoCommandRegistered;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.rmq.RMQApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VinPlayBackendMain {
    private static final Logger logger = Logger.getLogger((String)"backend");
    private static final String LOG_PROPERTIES_FILE = "config/log4j.properties";
    private static String API_PORT = "8082";
    private static BaseController<HttpServletRequest, String> controller;
    public static int TYPE;
    private static String basePath;

    private static void initializeLogger() {
        Properties logProperties = new Properties();
        try {
            File file = new File(basePath.concat(LOG_PROPERTIES_FILE));
            logProperties.load(new FileInputStream(file));
            PropertyConfigurator.configure((Properties)logProperties);
            logger.info((Object)"Logging initialized.");
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load logging property config/log4j.properties");
        }
    }

    public static void main(String[] args) {
        try {
            basePath = VBeePath.initBasePath(VinPlayBackendMain.class);
            VinPlayBackendMain.initializeLogger();
            logger.debug((Object)"STARTING BACKEND API SERVER .... !!!!");
            // Config 
            PartnerConfig.ReadConfig();
            VinPlayBackendMain.loadCommands();
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            RMQApi.start((String)"config/rmq.properties");
            GameCommon.init();
            AgentUtils.init();
            GameBaiUtils.init();
            if (BackendUtils.apiRunTask()) {
                ReportMoneyUtils.init();
                CardOzzePendingUtils.init();
                ResetUserMissionUtils.init();
                UpdateCommissionUtils.init();
            }
            Server server = new Server(Integer.parseInt(API_PORT));
            ServletHandler handler = new ServletHandler();
            handler.addFilterWithMapping(CorsFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
            handler.addServletWithMapping(JeetyServlet.class, "/api_backend");
            server.setHandler((Handler)handler);
            server.start();
            logger.info((Object)"BACKEND API SERVER Started ...!!!");
            server.join();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info((Object)("BACKEND API SERVER Start error: " + e.getMessage()));
        }
    }

    private static void loadCommands() throws Exception {
        File file = new File(basePath.concat("config/api_backend.xml"));
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("backend");
        Element el = (Element)nodeList.item(0);
        API_PORT = el.getElementsByTagName("port").item(0).getTextContent();
        try {
            TYPE = Integer.parseInt(el.getElementsByTagName("type").item(0).getTextContent());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Element cmds = (Element)el.getElementsByTagName("commands").item(0);
        NodeList cmdList = cmds.getElementsByTagName("command");
        HashMap<Integer, String> commandsMap = new HashMap<Integer, String>();
        for (int i = 0; i < cmdList.getLength(); ++i) {
            Element eCmd = (Element)cmdList.item(i);
            Integer id = Integer.parseInt(eCmd.getElementsByTagName("id").item(0).getTextContent());
            String path = eCmd.getElementsByTagName("path").item(0).getTextContent();
            System.out.println(id + " <-> " + path);
            commandsMap.put(id, path);
        }
        controller = new BaseController();
        controller.initCommands(commandsMap);
    }

    static {
        TYPE = 0;
    }

    public static class JeetyServlet
    extends HttpServlet {
        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            this.onExecute(request, response);
        }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            this.onExecute(request, response);
        }

        private void onExecute(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);
            Map requestMap = request.getParameterMap();
            if (requestMap.containsKey("c")  || requestMap.containsKey("chargeId")) {
                String command = request.getParameter("c");
                if (command == null || command.equalsIgnoreCase("")){
                    command = "11029";
                }
                Param param = new Param();
                param.set((Object)request);
                //logger.debug((Object)("command: " + command));

                try {
                    response.getWriter().println((String)controller.processCommand(Integer.valueOf(Integer.parseInt(command)), param));
                }
                catch (NoCommandRegistered e2) {
                    logger.debug((Object)"COMMAND NOT FOUND");
                    response.getWriter().println("COMMAND NOT FOUND");
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println(e1);
                    response.getWriter().println("EXCEPTION: " + e1.getMessage());
                }
            } else {
                response.getWriter().println("NO COMMANDS PARAMETERS");
            }
        }
    }

}

