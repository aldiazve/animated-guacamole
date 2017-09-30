#!/usr/bin/python
import sys

DEBUG = False
inp = sys.stdin.read()

def primeros(rule, rules, non_terminals):
	if len(rule) == 1 and rule[0]=="" or len(rule) == 0: # not sure for == 0, used for siguientes
		return set(["epsilon"])
	if rule[0] not in non_terminals:
		return set([rule[0]])
	else: # no terminal
		a = set()
		for r in rules[rule[0]]:
			p = primeros(r, rules, non_terminals)

			a = a.union(p - set(["epsilon"]))
			if "epsilon" in p:
				if len(rule) == 1:
					a.add("epsilon")
				else:
					a = a.union(primeros(rule[1:], rules, non_terminals))
		return a
# http://hypertextbookshop.com/transPL/Contents/01_Topics/03_Parsing/04_Section_4/02_page_2_-_First_Follow_and_Predict%20Sets.html
def siguientes(simboloInicial, rules, non_terminals):
	sig = {NT:set() for NT in non_terminals}
	sig[simboloInicial].add("$")

	cambios = True

	while cambios:
		cambios = False
		reglas = [(r, rule) for r in rules for rule in rules[r]]
		for (Rn, Rd) in reglas:
			for i in range(len(Rd)):
				if Rd[i] not in non_terminals:
					continue
				# for each non terminal in the rule
				siguientes = Rd[i+1:]
				ps = primeros(siguientes, rules, non_terminals)
				epsi = "epsilon" in ps
				ps.discard("epsilon")
				prev_len = len(sig[Rd[i]])
				sig[Rd[i]] = sig[Rd[i]].union(ps)
				if epsi:
					sig[Rd[i]] = sig[Rd[i]].union(sig[Rn])
				if prev_len != len(sig[Rd[i]]):
					cambios = True
	return sig	
if DEBUG:
        print(inp)
rules = {}
simboloInicial = None
for c in inp.splitlines():
	# R -> rule
	if '->' not in c:
		continue
	R, rule = map(lambda x: x.strip(), c.split("->"))
	rule = map(lambda x: x.strip(), rule.split(" "))
	if simboloInicial is None:
		simboloInicial = R
	if R not in rules:
		rules[R] = set()
	rules[R].add(tuple(rule))

non_terminals = set(rules.keys())
if DEBUG:
        print(non_terminals)
        print("Inicial: ", simboloInicial)
        print(rules)
        
        print("""
        Prim:
        
	""")
primeros_table = {}
for r in rules:
	for rule in rules[r]:
		primeros_table[(r, rule)] = primeros(rule, rules, non_terminals)
                if DEBUG:
		        print((r, rule), primeros_table[(r, rule)])


siguientes_table=siguientes(simboloInicial, rules, non_terminals)
if DEBUG:
        print("""
Sig:

	""")
        print(siguientes_table)


        print("""
Pred:

	""")

pred_table = {}
for r in rules:
	for rule in rules[r]:
		a = (r, rule)
		if "epsilon" in primeros_table[a]:
			pred_table[a] = (primeros_table[a]-set(["epsilon"])).union(siguientes_table[a[0]])
		else:
			pred_table[a] = primeros_table[a]
                if DEBUG:
                        print(a, pred_table[a])

     
print("""class SyntacticAnalyzer{
private List<Token> tokens;
public SyntacticAnalyzer(List<Token> tks){
tokens = tks;
}
private String token(){
return tokens.get(0).getType();
}
private void emparejar(String t){
System.out.println("\tEmparejar '"+t+"'");

if(tokens.get(0).getType().equals(t)){
tokens.remove(0);
}else{
System.out.println("Mismatch: received "+tokens.get(0).getType()+", expected "+t+"");
System.exit(1);
}
}

void error_sintactico(String... values){

System.out.print("err sintactico - expected:");
for(String s:values){
System.out.print(" "+s);
}
System.out.println("");
System.exit(1);
}

public void analyze(){
"""+simboloInicial+"""();

if(tokens.size()>1)
{
System.out.println("there are tokens left to process... fail.");
System.exit(1);
}
System.out.println("Success :)");
}

""")
for r in rules:
	print("void " +r+"(){")
	print("System.out.println(\"Rule: "+r+" token: \"+token());")
	preds = set()
	for rule in rules[r]:
		a = (r, rule)
		preds = preds.union(pred_table[a])

		print("if(token().equals(" + ") || token().equals(".join(['"'+x+'"' for x in pred_table[a]]) + ")) { ")

		for elem in rule:
			if elem in non_terminals:
				print(elem+"();")
			elif elem == "epsilon":
				print("//epsilon")
				continue
			else:
				print("emparejar(\""+elem+"\");")
		print("return;\n}")
        print("error_sintactico("+",".join(['"'+x+'"' for x in preds])+");\n")
        print("}\n")
print("}")

