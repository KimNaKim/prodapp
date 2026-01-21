package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RequestDTO;
import dto.ResponseDTO;

public class MyServer {
    public static void main(String[] args) {


        try {
            //1. 20000번 포트로 대기중
            ServerSocket ss = new ServerSocket(20000);
            Socket socket = ss.accept();

            //2. 새로운 소켓에 버퍼를 달기(BR, BW)
            //br 버퍼
            InputStream in = socket.getInputStream();
            InputStreamReader ir = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(ir);
            //bw 버퍼
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter ow = new OutputStreamWriter(out);
            BufferedWriter bw = new BufferedWriter(ow);

            while(true){
                //1. client로부터 입력받기
                String line = br.readLine();
                System.out.println("[from client] : " + line);

                if(line.equals("exit")){
                    System.out.println("프로그램을 종료합니다.");
                    break;
                }

                //gson객체 생성
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .create();
                RequestDTO req = gson.fromJson(line, RequestDTO.class);
                ResponseDTO resp = new ResponseDTO();
                ProductService ps = new ProductService();

                //3. Service 호출하기
                if(req.getMethod().equals("get")){
                    if(req.getQuerystring()!=null){
                        Integer id = req.getQuerystring().get("id");
                        //상품상세
                        if(ps.findById(id) == null){
                            resp.setMsg("id not found");
                        } else{
                            resp.setMsg("ok");
                        }
                        resp.setBody(ps.findById(id));
                    }else{
                        //상품목록
                        resp.setBody(ps.findAll());
                        if(resp.getBody() == null){
                            //data가 존재하지 않을 때
                            resp.setMsg("Data is not exist");
                        }else{
                            resp.setMsg("ok");
                        }
                    }
                }

                if(req.getMethod().equals("post")){
                    //상품등록
                    if (req.getProduct() != null) {
                        String name = (String) req.getProduct().getName();
                        Integer price = req.getProduct().getPrice();
                        Integer qty = req.getProduct().getQty();
                        //상품등록
                        ps.save(name, price, qty);
                        resp.setMsg("ok");
                    }
                    else{
                        //body 값이 없을 때
                        resp.setMsg("body not found");
                    }
                }

                if(req.getMethod().equals("delete")){
                    //삭제
                    if(req.getQuerystring()!=null){
                        Integer id = req.getQuerystring().get("id");
                        int isDelete = ps.deleteById(id);
                        if(isDelete == 0){
                            resp.setMsg("id not found");
                        }else if(isDelete == -1){
                            resp.setMsg("delete failed");
                        }else{
                            resp.setMsg("ok");
                        }
                    }
                    else{
                        //querystring 값이 없을 때 (id가 없을 때)
                        resp.setMsg("id not found");
                    }
                }

                String json = gson.toJson(resp);
                bw.write(json);
                bw.write("\n");
                bw.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}