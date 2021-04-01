package formulas;

import java.util.List;

public class Symbol {
	private int id;
	private String symbolTex;
	private List formulas;

	public Symbol () {}
	
	public Symbol (String symbol_tex)
	{
		this.symbolTex = symbol_tex;
	}
	
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSymbolTex()
    {
        return symbolTex;
    }

    public void setSymbolTex(String symbol_tex)
    {
        this.symbolTex = symbol_tex;
    }

    public List getFormulas()
    {
        return formulas;
    }

    public void setFormulas(List formulas)
    {
        this.formulas = formulas;
    }
    
    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
        if (!this.getClass().equals(obj.getClass())) return false;

        Symbol obj2 = (Symbol)obj;
        if((this.id == obj2.getId()) && (this.symbolTex.equals(obj2.getSymbolTex()))) {
           return true;
        }
        return false;
    }
    
    public int hashCode()
    {
    	int tmp = 0;
    	tmp = (id + symbolTex).hashCode();
    	return tmp;
    }

}
