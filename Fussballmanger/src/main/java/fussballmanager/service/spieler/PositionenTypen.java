package fussballmanager.service.spieler;

public enum PositionenTypen {

	TW("Torwart", 1, RollenTypen.TORWART),
	LV("Linker Verteidiger", 2, RollenTypen.VERTEIDIGER),
	LIV("Linker Innenverteidiger", 3, RollenTypen.VERTEIDIGER),
	LIB("Libero", 4, RollenTypen.VERTEIDIGER),
	RIV("Rechter Innenverteidiger", 5, RollenTypen.VERTEIDIGER),
	RV("Rechter Verteidiger", 6, RollenTypen.VERTEIDIGER),
	LM("Linkes Mittelfeld", 7, RollenTypen.MITTELFELD),
	DM("Defensives Mittelfeld", 8, RollenTypen.MITTELFELD),
	RM("Rechtes Mittelfeld", 9, RollenTypen.MITTELFELD),
	ZM("Zentrales Mittelfeld", 10, RollenTypen.MITTELFELD),
	OM("Offensives Mittelfeld", 11, RollenTypen.MITTELFELD),
	LS("Linker Stürmer", 12, RollenTypen.ANGREIFER),
	MS("Mittelstürmer", 13, RollenTypen.ANGREIFER),
	RS("Rechter Stürmer", 14, RollenTypen.ANGREIFER);    
    private final String positionsName;
    private final int rangfolge;
    private final RollenTypen rollenTyp;
    
    PositionenTypen(String positionsName, int rangfolge, RollenTypen rollenTyp){
    	this.positionsName = positionsName;
    	this.rangfolge = rangfolge;
    	this.rollenTyp = rollenTyp;
    }
    
    public String getPositionsName() {
    	return this.positionsName;
    }
    
    public int getRangfolge() {
    	return this.rangfolge;
    }

	public RollenTypen getRollenTyp() {
		return rollenTyp;
	}
}
