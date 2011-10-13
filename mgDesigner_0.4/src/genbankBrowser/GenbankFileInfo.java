package genbankBrowser;

import java.io.*;
import java.util.*;
import org.biojava.bio.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;

class GenbankFileInfo {
	String locus;
	String circular;
	int seqLen;
	Vector nameList = new Vector();		// 유전자 이름 목록
	Vector startList = new Vector();	// 유전지 시작 위치 목록
	Vector endList = new Vector();		// 유전자 끝 위치 목록

	GenbankFileInfo(String filename) {
		try {
			BufferedReader br 
				= new BufferedReader(new FileReader(filename));
			SequenceIterator seq_iter = SeqIOTools.readGenbank(br);
			Sequence seq = seq_iter.nextSequence();

			// locus 가져오기
			locus = (String)(seq.getAnnotation().getProperty("LOCUS"));
			
			// circular 여부 가져오기
			circular = (String)(seq.getAnnotation().getProperty("CIRCULAR"));
			
			// 서열 길이 가져오기
			seqLen = Integer.parseInt(
					(String)seq.getAnnotation().getProperty("SIZE"));

			// 유전자 이름,시작/끝 위치 저장
			Iterator feat_iter = seq.features();
			while(feat_iter.hasNext()){
				Feature f = (Feature)(feat_iter.next());
				if(f.getType().equals("gene")) {
					Annotation annot = f.getAnnotation();
					Location loc = f.getLocation();
					if(annot.containsProperty("gene")) {
						nameList.add(annot.getProperty("gene"));
						startList.add(new Integer(loc.getMin()));
						endList.add(new Integer(loc.getMax()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 유전자 개수 반환
	int getCount() {
		return nameList.size();
	}

	// 유전자 시작위치 목록 반환
	Vector getStartPositions() {
		return startList;
	}

	// 유전자 끝 위치 목록 반환
	Vector getEndPositions() {
		return endList;
	}

	// 유전자 이름 목록 반환
	Vector getGeneNames() {
		return nameList;
	}

	// 서열 길이 반환
	int getSeqLen() {
		return seqLen;
	}

	// 유전자 이름 반환
	String getGeneName(int index) {
		return (String)(nameList.get(index));
	}

	// 유전자 시작 위치 반환
	int getStartPos(int index) {
		return ((Integer)(startList.get(index))).intValue();
	}

	// 유전자 끝위치 반환
	int getEndPos(int index) {
		return ((Integer)(endList.get(index))).intValue();
	}

	public static void main(String[] args) {
		GenbankFileInfo gbInfo = new GenbankFileInfo(args[0]);
		for(int i=0; i<gbInfo.getCount(); i++) {
			System.out.println(
				gbInfo.getGeneName(i) +" : "+ 
				gbInfo.getStartPos(i) +", "+ 
				gbInfo.getEndPos(i));
		}
	}
}
