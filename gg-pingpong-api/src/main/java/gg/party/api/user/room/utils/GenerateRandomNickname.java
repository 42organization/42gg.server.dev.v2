package gg.party.api.user.room.utils;

import java.util.Random;

/*
 * 익명 닉네임을 만드는 클래스입니다.
 * PREFIX와 SUFFIX 배열에서 랜덤으로 단어를 선택하여 조합합니다.
 * 둘 중 중복되는 요소도 가능하게 작성했습니다. ex) 용감한 int, 용감한 string이 공존할 수 있습니다.
 */
public class GenerateRandomNickname {
	private static final String[] PREFIX = {"용감한", "대담한", "밝은", "뛰어난", "쾌활한", "영리한", "도망가는", "든든한",
		"대담한", "열심인", "힘찬", "불같은", "신선한", "관대한", "유쾌한", "행복한", "희망찬", "명랑한", "친절한", "활기찬",
		"착한", "이상한", "자랑스러운", "빠른", "안도하는", "수줍은", "어리석은", "강한", "감사하는", "힘든", "용감한", "열정적인",
		"똑똑한", "즐거운", "굉장한", "고수", "엄청난", "뜬금없는", "충무공", "제너럴", "엠페러", "마제스티", "판타스틱", "레전더리",
		"먼치킨", "다크호스", "슈퍼루키", "엘리트", "정점", "전교회장", "베테랑"};

	private static final String[] SUFFIX = {"list", "vector", "string", "boolean", "number", "object", "array",
		"set", "map", "date", "function", "null", "undefined", "buffer", "json", "error", "stack", "queue", "deque",
		"tree", "graph", "pair", "int", "char", "float", "double", "long", "longlong", "size_t", "short", "struct"};

	public static String generateRandomNickname() {
		Random random = new Random();
		String randomAdjective = PREFIX[random.nextInt(PREFIX.length)];
		String randomNoun = SUFFIX[random.nextInt(SUFFIX.length)];
		return randomAdjective + " " + randomNoun;
	}
}
