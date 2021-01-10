package sist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


public class LogAnalyzer {

	File file;
	BufferedReader br;
	
	List<String> logLineList; //file 안의 데이터를 줄 단위로 넣은 리스트
	
	List<String> resultCodeList; // 응답결과 리스트
	List<String> urlList; 	     // URL 리스트
	List<String> browserList;	 // 브라우저 종류 리스트
	List<String> dateTimeList;	 // 일자 시간 리스트
	
	final String LOG_PATH = "C:/log/sist_input_1.log"; //파일 경로
	
	LogAnalyzer() throws IOException {
		
		//파일 load
		file = new File(LOG_PATH);
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		
		//file안의 데이터를 한 줄 단위로 읽어들이기!!
		logLineList = new ArrayList<String>();
		String temp = "";
		
		while((temp=br.readLine()) != null) {
			logLineList.add(temp);
		}
		
		//Stream 닫기
		br.close();

		//file 안의 데이터를 (1)응답결과, (2)URL, (3)브라우저 종류, (4)일자 시간 네 부분으로 나누어 List에 담기 
		resultCodeList = new ArrayList<String>();
		urlList = new ArrayList<String>();
		browserList = new ArrayList<String>();
		dateTimeList = new ArrayList<String>();
		
		for( int i=0; i<logLineList.size(); i++) {
			StringTokenizer tokenizer = new StringTokenizer(logLineList.get(i), "[]");
			
			while(tokenizer.hasMoreTokens()) {
				resultCodeList.add(tokenizer.nextToken());
				urlList.add(tokenizer.nextToken());
				browserList.add(tokenizer.nextToken());
				dateTimeList.add(tokenizer.nextToken());
			}
		}
	}//LogAnalyzer
	
	//5번 문제
	public void unusualRequest() {
		
		//비정상적인 요청이 발생한 횟수 : count
		int count = 0; 
		for(int i=0; i<resultCodeList.size(); i++) {
			if("403".equals(resultCodeList.get(i))) {
				count++;
			}//if
		}//for
		
		//비정상적인 요청이 발생한 비율 : rate
		double rate = ((double)count/resultCodeList.size())*100;
		
		System.out.println("비정상적인 요청이 발생한 횟수 : " + count);
		System.out.printf("비정상적인 요청이 발생한 비율 : %.2f %n", rate); 
	}//unusualRequest
	
	
	//6번 문제
	public void mostUsedKey(int startLine, int endLine) {//startLine : 범위 시작 줄, endLine : 범위 마지막 줄
		
		//Map (key : key의 이름, value : key의 사용된 횟수)
		Map<String, Integer> countOfUsedKey = new HashMap<String, Integer>();
		
		//key 뽑아내기
		for (int i=startLine-1; i<endLine; i++) {
			String url = urlList.get(i);
			
			Map<String, String> keyValueMap = extractQueryForURL(url);
			
			Set<String> keySet = keyValueMap.keySet(); 
			Iterator<String> iterator = keySet.iterator();
			
			while(iterator.hasNext()) {
				String key = iterator.next();
				String value = keyValueMap.get(key);
				
				if("key".equals(key)) {
					if(countOfUsedKey.containsKey(value)) {//countOfUsedKey 안에 이미 들어가있다면
						//해당하는 key의 value값을 +1
						countOfUsedKey.put(value,countOfUsedKey.get(value)+1);
					}else{//countOfUsedKey 안에 없는 key라면
						//key 추가
						countOfUsedKey.put(value, 1); 
					}//end else
				}//end id
			}//end while
			
		}//end for
		
		//Map의 key 모음
		Set<String> countKeySet = countOfUsedKey.keySet();
		
		//Set의 검색을 위한 반복자 Iterator
		Iterator<String> iterator = countKeySet.iterator();
		
		//가장 많이 사용된 키 : maxKey, maxKey의 사용 횟수 : maxValue
		String maxKey = "";
		int maxValue = 0;
		
		while(iterator.hasNext()) {
			String key = iterator.next();
			int value = countOfUsedKey.get(key);
			
			if(value > maxValue) {
				maxKey = key;
				maxValue = value;
			}
		}
		System.out.println("여기가 바뀌었따!!");
		System.out.println("가장 많이 사용한 key : " + maxKey + ", 사용 횟수 : " + countOfUsedKey.get(maxKey));
		
	}//mostUsed
	
	//6번 문제.url을 입력하면 value값으로 key의 종류를 담은 keyValueMap 을 반환.
	Map<String, String> extractQueryForURL(String url){ 
		
		Map<String, String> keyValueMap = new HashMap<>();
		
		//urlList를 '?' 기준으로 자름
		//결과 예시 : key=mongodb&query=sist
		int questionIndex = url.indexOf("?");
		String queryString = url.substring(questionIndex + 1);
		
		//'&' 기준으로 더 자름
		//결과 예시 : key=mongodb
		String[] keyValues = queryString.split("&");
		
		//'=' 기준으로 더 자름
		//결과 예시 : key = key, value = mongodb
		for(int j=0; j<keyValues.length; j++) {
			String keyValue[] = keyValues[j].split("=");
			String key = keyValue[0];
			String value = keyValue[1];
			keyValueMap.put(key, value);
		}
		
		return keyValueMap;
	}

}//class
