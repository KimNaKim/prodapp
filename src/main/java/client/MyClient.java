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
                System.out.println("1. 상품목록");
                System.out.println("2. 상품상세");
                System.out.println("3. 상품삭제");
                System.out.println("4. 상품등록");
                System.out.print("번호를 입력하세요. : ");

                //요청하기
                String keyboardData = keyBuf.readLine();
                if(keyboardData.equals("exit")){
                    System.out.println("프로그램을 종료합니다.");
                    bw.write("exit");
                    bw.write("\n");
                    bw.flush();
                    break;
                }

                //2. JSON 데이터(String)를 java 오브젝트로 파싱하기
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .create();
                RequestDTO requestDTO = new RequestDTO();

                if(keyboardData.equals("1")){
                    //상품조회
                    requestDTO.setMethod("get");
                } else if(keyboardData.equals("2")){
                    //상품상세
                    requestDTO.setMethod("get");
                    System.out.print("검색하고 싶은 id를 입력하세요 : ");
                    keyboardData = keyBuf.readLine();
                    Map<String, Object> query = new HashMap<>();
                    query.put("id", Integer.parseInt(keyboardData));
                    requestDTO.setQuerystring(query);
                } else if(keyboardData.equals("3")){
                    //상품삭제
                    requestDTO.setMethod("delete");
                    System.out.print("삭제하고 싶은 id를 입력하세요 : ");
                    keyboardData = keyBuf.readLine();
                    Map<String, Object> query = new HashMap<>();
                    query.put("id", Integer.parseInt(keyboardData));
                    requestDTO.setQuerystring(query);
                }else if(keyboardData.equals("4")){
                    //상품등록
                    requestDTO.setMethod("post");
                    System.out.print("name price qty 입력: ");
                    keyboardData = keyBuf.readLine();
                    String[] parts = keyboardData.split(" ");

                    Map<String, Object> body = new HashMap<>();
                    body.put("name", parts[0]);
                    body.put("price", Integer.parseInt(parts[1]));
                    body.put("qty", Integer.parseInt(parts[2]));

                    requestDTO.setBody(body);
                }
                
                String json = gson.toJson(requestDTO);
                bw.write(json);
                bw.write("\n");
                bw.flush();

                String rs = br.readLine();
                System.out.println(rs.replace("{", "\n{"));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}