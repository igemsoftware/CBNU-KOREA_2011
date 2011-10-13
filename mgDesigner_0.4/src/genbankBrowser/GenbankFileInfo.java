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
	Vector nameList = new Vector();		// ������ �̸� ���
	Vector startList = new Vector();	// ������ ���� ��ġ ���
	Vector endList = new Vector();		// ������ �� ��ġ ���

	GenbankFileInfo(String filename) {
		try {
			BufferedReader br 
				= new BufferedReader(new FileReader(filename));
			SequenceIterator seq_iter = SeqIOTools.readGenbank(br);
			Sequence seq = seq_iter.nextSequence();

			// locus ��������
			locus = (String)(seq.getAnnotation().getProperty("LOCUS"));
			
			// circular ���� ��������
			circular = (String)(seq.getAnnotation().getProperty("CIRCULAR"));
			
			// ���� ���� ��������
			seqLen = Integer.parseInt(
					(String)seq.getAnnotation().getProperty("SIZE"));

			// ������ �̸�,����/�� ��ġ ����
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

	// ������ ���� ��ȯ
	int getCount() {
		return nameList.size();
	}

	// ������ ������ġ ��� ��ȯ
	Vector getStartPositions() {
		return startList;
	}

	// ������ �� ��ġ ��� ��ȯ
	Vector getEndPositions() {
		return endList;
	}

	// ������ �̸� ��� ��ȯ
	Vector getGeneNames() {
		return nameList;
	}

	// ���� ���� ��ȯ
	int getSeqLen() {
		return seqLen;
	}

	// ������ �̸� ��ȯ
	String getGeneName(int index) {
		return (String)(nameList.get(index));
	}

	// ������ ���� ��ġ ��ȯ
	int getStartPos(int index) {
		return ((Integer)(startList.get(index))).intValue();
	}

	// ������ ����ġ ��ȯ
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
