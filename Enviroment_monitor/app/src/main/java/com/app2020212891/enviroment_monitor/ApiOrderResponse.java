package com.app2020212891.enviroment_monitor;

public class ApiOrderResponse {
        private String msg;
        private int code;
        private Data data;

        public void SetApiOrderResponse(String msg, int code) {
            this.msg = msg;
            this.code = code;
        }

        public  void SetApiOrderResponseData(Data data){
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public Data getData() {
            return data;
        }

        public static class Data {
            private String cmd_uuid;

            public void setCmd_uuid(String cmd_uuid) {
                this.cmd_uuid = cmd_uuid;
            }

            public String getCmdUuid() {
                return cmd_uuid;
            }
        }
}
