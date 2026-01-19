package client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RequestDTO;
import dto.ResponseDTO;

public class MyClient {
    public static void main(String[] args) {
        try {
            //소켓 연결
            Socket socket = new Socket("localhost", 20000);

            //1. 키보드에게 입력받기 버퍼
            InputStream keyStream = System.in;
            InputStreamReader keyReader = new InputStreamReader(keyStream);
            BufferedReader keyBuf = new BufferedReader(keyReader);

            //2.쓰기버퍼
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter ow = new OutputStreamWriter(out);
            BufferedWriter bw = new BufferedWriter(ow);

            //3. br버
            InputStream in = socket.getInputStream();
            InputStreamReader ir = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(ir);

            while(true){
                //클라이언트에게 서비스 종류 안내하기
                System.out.println("종료하려면 exit를 입력하세요.");
                System.out.print("정보를 입력하세요. : ");

                //종료요청하기
                String keyboardData = keyBuf.readLine();
                if(keyboardData.equals("exit")){
                    System.out.println("프로그램을 종료합니다.");
                    bw.write("exit");
                    bw.write("\n");
                    bw.flush();
                    break;
                }

                //2. JSON 데이터(String)를 java 오브젝트로 파싱하기
                //gson객체 생성, req생성
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .create();
                RequestDTO req = new RequestDTO();

                String[] parts = keyboardData.split(" ");
                String method = parts[0];
                req.setMethod(method);

                if (method.equals("get")) {

                    if (parts.length == 1) {
                        // get → 전체 조회
                        // querystring 없음
                    }

                    if (parts.length == 2) {
                        // get 3 → 단건 조회
                        Map<String, Object> query = new HashMap<>();
                        query.put("id", Integer.parseInt(parts[1]));
                        req.setQuerystring(query);
                    }
                }

                if (method.equals("delete")) {
                    if (parts.length == 2) {
                        Map<String, Object> query = new HashMap<>();
                        query.put("id", Integer.parseInt(parts[1]));
                        req.setQuerystring(query);
                    }
                }

                if (method.equals("post")) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("name", parts[1]);
                    body.put("price", Integer.parseInt(parts[2]));
                    body.put("qty", Integer.parseInt(parts[3]));
                    req.setBody(body);
                }

                String json = gson.toJson(req);
                bw.write(json);
                bw.write("\n");
                bw.flush();

                String rs = br.readLine();
                System.out.println(rs.replace("}", "}\n")
                        .replace("[","\n["));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}