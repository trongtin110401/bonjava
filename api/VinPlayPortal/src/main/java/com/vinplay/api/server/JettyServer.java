/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.LogPortalService
 *  com.vinplay.dal.service.impl.LogPortalServiceImpl
 *  com.vinplay.dal.utils.PotUtils
 *  com.vinplay.vbee.common.cp.BaseController
 *  com.vinplay.vbee.common.cp.NoCommandRegistered
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastLoader
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.utils.UserValidaton
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PropertyConfigurator
 *  org.eclipse.jetty.server.ConnectionFactory
 *  org.eclipse.jetty.server.Connector
 *  org.eclipse.jetty.server.Handler
 *  org.eclipse.jetty.server.HttpConfiguration
 *  org.eclipse.jetty.server.HttpConfiguration$Customizer
 *  org.eclipse.jetty.server.HttpConnectionFactory
 *  org.eclipse.jetty.server.SecureRequestCustomizer
 *  org.eclipse.jetty.server.Server
 *  org.eclipse.jetty.server.ServerConnector
 *  org.eclipse.jetty.server.SslConnectionFactory
 *  org.eclipse.jetty.server.handler.HandlerCollection
 *  org.eclipse.jetty.servlet.FilterHolder
 *  org.eclipse.jetty.servlet.ServletHandler
 *  org.eclipse.jetty.servlet.ServletHolder
 *  org.eclipse.jetty.util.ssl.SslContextFactory
 */
package com.vinplay.api.server;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.vinplay.api.processors.vippoint.TopVippoint;
import com.vinplay.api.server.CorsFilter;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.dal.service.LogPortalService;
import com.vinplay.dal.service.impl.LogPortalServiceImpl;
import com.vinplay.dal.utils.PotUtils;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.cp.BaseController;
import com.vinplay.vbee.common.cp.NoCommandRegistered;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.UserValidaton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JettyServer {

    private static final Logger logger = Logger.getLogger((String) "api");
    private static final Logger blackListIpLogger = Logger.getLogger((String) "BlackListIpLogger");
    private static final String LOG_PROPERTIES_FILE = "config/log4j.properties";
    private static LogPortalService service = new LogPortalServiceImpl();
    private static String API_PORT = "8081";
    private static String SSL_PORT = "8443";
    private static BaseController<HttpServletRequest, String> controller;
    private static String basePath;

    private static void initializeLogger() {
        Properties logProperties = new Properties();
        try {
            File file = new File(basePath.concat(LOG_PROPERTIES_FILE));
            logProperties.load(new FileInputStream(file));
            PropertyConfigurator.configure((Properties) logProperties);
            logger.info((Object) "Logging initialized.");
        } catch (IOException e) {
            throw new RuntimeException("Unable to load logging property config/log4j.properties");
        }
    }

    public static void main(String[] args) {
        try {
            basePath = VBeePath.initBasePath(JettyServer.class);
            JettyServer.initializeLogger();
            logger.debug((Object) "STARTING PORTAL API SERVER .... !!!!");
            JettyServer.loadCommands();
            RMQApi.start((String) "config/rmq.properties");
            // Config 
            PartnerConfig.ReadConfig();
            logger.debug((Object) PartnerConfig.ESMSApiKey);
            logger.debug((Object) PartnerConfig.ESMSSecretKey);
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            UserValidaton.init();
            PortalUtils.loadGameConfig();
            PotUtils.init();
            TopVippoint.init();
            int port = Integer.parseInt(API_PORT);
            int sslPort = Integer.parseInt(SSL_PORT);
            Server server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            connector.setIdleTimeout(30000L);
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer((HttpConfiguration.Customizer) new SecureRequestCustomizer());
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(basePath.concat("config/vinplay.jks"));
            sslContextFactory.setKeyStorePassword("cardgame@123!");
            sslContextFactory.setKeyManagerPassword("cardgame@123!");
            ServerConnector sslConnector = new ServerConnector(server, new ConnectionFactory[]{new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https)});
            sslConnector.setPort(sslPort);
            ServletHandler handler = new ServletHandler();
            handler.addFilterWithMapping(CorsFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
            handler.addServletWithMapping(JeetyServlet.class, "/api");
            HandlerCollection handlerCollection = new HandlerCollection();
            handlerCollection.setHandlers(new Handler[]{handler});
            server.setHandler((Handler) handlerCollection);
            server.setConnectors(new Connector[]{connector, sslConnector});
            server.start();
            logger.info((Object) "PORTAL API SERVER Started ...!!!");
            server.join();
        } catch (Exception e) {
            logger.info((Object) ("PORTAL API SERVER Start error: " + e.getMessage()));
            e.printStackTrace();
        } finally {
            // Xóa cache
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap<String, String> map = instance.getMap("LOGIN_OTHER_DEVICE_MAP");
            map.clear();
        }
    }

    private static void loadCommands() throws Exception {
        File file = new File(basePath.concat("config/api_portal.xml"));
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("portal");
        Element el = (Element) nodeList.item(0);
        API_PORT = el.getElementsByTagName("port").item(0).getTextContent();
        SSL_PORT = el.getElementsByTagName("ssl_port").item(0).getTextContent();
        Element cmds = (Element) el.getElementsByTagName("commands").item(0);
        NodeList cmdList = cmds.getElementsByTagName("command");
        HashMap<Integer, String> commandsMap = new HashMap<Integer, String>();
        for (int i = 0; i < cmdList.getLength(); ++i) {
            Element eCmd = (Element) cmdList.item(i);
            Integer id = Integer.parseInt(eCmd.getElementsByTagName("id").item(0).getTextContent());
            String path = eCmd.getElementsByTagName("path").item(0).getTextContent();
            logger.debug((Object) (id + " <-> " + path));
            System.out.println(id + " <-> " + path);
            commandsMap.put(id, path);
        }
        controller = new BaseController();
        controller.initCommands(commandsMap);
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
            String remoteAddr = request.getRemoteAddr();
            logger.info((Object) ("IP:" + remoteAddr));
            if (requestMap.containsKey("c")) {
                String command = request.getParameter("c");
                if (command == null || command.equalsIgnoreCase("")) {
                    blackListIpLogger.debug((Object) remoteAddr);
                    return;
                }
                Param param = new Param();
                param.set((Object) request);
                logger.debug((Object) ("command: " + command));
                try {
                    if (!"8000".equals(command)) {
                        response.getWriter().println((String) controller.processCommand(Integer.valueOf(Integer.parseInt(command)), param));
                        service.log(command);
                    } else {
                        response.setContentType("text/csv");
                        response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
                        OutputStream outputStream = response.getOutputStream();
                        String outputResult = (String) controller.processCommand(Integer.valueOf(Integer.parseInt(command)), param);
                        outputStream.write(outputResult.getBytes());
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (NoCommandRegistered e2) {
                    logger.debug((Object) "COMMAND NOT FOUND");
                    response.getWriter().println("COMMAND NOT FOUND");
                    service.log("CMD_404");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println(e1);
                    response.getWriter().println("EXCEPTION: " + e1.getMessage());
                }
            } else {
                blackListIpLogger.debug((Object) remoteAddr);
                response.getWriter().println("NO COMMANDS PARAMETERS");
                service.log("NO_CMD");
            }
        }
    }

}

