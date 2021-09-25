package com.sachin.mqReadWrite.properties;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mqdetails")
@Getter
@Setter
@ToString

public class MQProperties {
   private Cluster cluster;
   private Qmgrs qmgrs;
   private List<String> queues;

   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor
   public static class Cluster {
      private String serverhost;
      private String port;
      private String user;
      private String pass;
      private String ssl;
   }

   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor
   public static class Qmgrs {
      private String qmgrname;
      private String channel;

   }

}


