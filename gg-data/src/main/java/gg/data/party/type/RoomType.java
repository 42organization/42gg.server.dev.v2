package gg.data.party.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
	OPEN,   // Ordinal 0
	START,  // Ordinal 1
	FINISH, // Ordinal 2
	HIDDEN  // Ordinal 3
}
