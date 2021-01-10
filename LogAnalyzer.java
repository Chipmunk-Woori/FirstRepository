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
	
	List<String> logLineList; //file ���� �����͸� �� ������ ���� ����Ʈ
	
	List<String> resultCodeList; // ������ ����Ʈ
	List<String> urlList; 	     // URL ����Ʈ
	List<String> browserList;	 // ������ ���� ����Ʈ
	List<String> dateTimeList;	 // ���� �ð� ����Ʈ
	
	final String LOG_PATH = "C:/log/sist_input_1.log"; //���� ���
	
	LogAnalyzer() throws IOException {
		
		//���� load
		file = new File(LOG_PATH);
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		
		//file���� �����͸� �� �� ������ �о���̱�!!
		logLineList = new ArrayList<String>();
		String temp = "";
		
		while((temp=br.readLine()) != null) {
			logLineList.add(temp);
		}
		
		//Stream �ݱ�
		br.close();

		//file ���� �����͸� (1)������, (2)URL, (3)������ ����, (4)���� �ð� �� �κ����� ������ List�� ��� 
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
	
	//5�� ����
	public void unusualRequest() {
		
		//���������� ��û�� �߻��� Ƚ�� : count
		int count = 0; 
		for(int i=0; i<resultCodeList.size(); i++) {
			if("403".equals(resultCodeList.get(i))) {
				count++;
			}//if
		}//for
		
		//���������� ��û�� �߻��� ���� : rate
		double rate = ((double)count/resultCodeList.size())*100;
		
		System.out.println("���������� ��û�� �߻��� Ƚ�� : " + count);
		System.out.printf("���������� ��û�� �߻��� ���� : %.2f %n", rate); 
	}//unusualRequest
	
	
	//6�� ����
	public void mostUsedKey(int startLine, int endLine) {//startLine : ���� ���� ��, endLine : ���� ������ ��
		
		//Map (key : key�� �̸�, value : key�� ���� Ƚ��)
		Map<String, Integer> countOfUsedKey = new HashMap<String, Integer>();
		
		//key �̾Ƴ���
		for (int i=startLine-1; i<endLine; i++) {
			String url = urlList.get(i);
			
			Map<String, String> keyValueMap = extractQueryForURL(url);
			
			Set<String> keySet = keyValueMap.keySet(); 
			Iterator<String> iterator = keySet.iterator();
			
			while(iterator.hasNext()) {
				String key = iterator.next();
				String value = keyValueMap.get(key);
				
				if("key".equals(key)) {
					if(countOfUsedKey.containsKey(value)) {//countOfUsedKey �ȿ� �̹� ���ִٸ�
						//�ش��ϴ� key�� value���� +1
						countOfUsedKey.put(value,countOfUsedKey.get(value)+1);
					}else{//countOfUsedKey �ȿ� ���� key���
						//key �߰�
						countOfUsedKey.put(value, 1); 
					}//end else
				}//end id
			}//end while
			
		}//end for
		
		//Map�� key ����
		Set<String> countKeySet = countOfUsedKey.keySet();
		
		//Set�� �˻��� ���� �ݺ��� Iterator
		Iterator<String> iterator = countKeySet.iterator();
		
		//���� ���� ���� Ű : maxKey, maxKey�� ��� Ƚ�� : maxValue
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
		System.out.println("���Ⱑ �ٲ����!!");
		System.out.println("���� ���� ����� key : " + maxKey + ", ��� Ƚ�� : " + countOfUsedKey.get(maxKey));
		
	}//mostUsed
	
	//6�� ����.url�� �Է��ϸ� value������ key�� ������ ���� keyValueMap �� ��ȯ.
	Map<String, String> extractQueryForURL(String url){ 
		
		Map<String, String> keyValueMap = new HashMap<>();
		
		//urlList�� '?' �������� �ڸ�
		//��� ���� : key=mongodb&query=sist
		int questionIndex = url.indexOf("?");
		String queryString = url.substring(questionIndex + 1);
		
		//'&' �������� �� �ڸ�
		//��� ���� : key=mongodb
		String[] keyValues = queryString.split("&");
		
		//'=' �������� �� �ڸ�
		//��� ���� : key = key, value = mongodb
		for(int j=0; j<keyValues.length; j++) {
			String keyValue[] = keyValues[j].split("=");
			String key = keyValue[0];
			String value = keyValue[1];
			keyValueMap.put(key, value);
		}
		
		return keyValueMap;
	}

}//class
