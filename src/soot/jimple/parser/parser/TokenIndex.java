package soot.jimple.parser.parser;

import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

class TokenIndex extends AnalysisAdapter
{
    int index;

    public void caseTAbstract(TAbstract node)
    {
        index = 0;
    }

    public void caseTFinal(TFinal node)
    {
        index = 1;
    }

    public void caseTNative(TNative node)
    {
        index = 2;
    }

    public void caseTPublic(TPublic node)
    {
        index = 3;
    }

    public void caseTProtected(TProtected node)
    {
        index = 4;
    }

    public void caseTPrivate(TPrivate node)
    {
        index = 5;
    }

    public void caseTStatic(TStatic node)
    {
        index = 6;
    }

    public void caseTSynchronized(TSynchronized node)
    {
        index = 7;
    }

    public void caseTTransient(TTransient node)
    {
        index = 8;
    }

    public void caseTVolatile(TVolatile node)
    {
        index = 9;
    }

    public void caseTClass(TClass node)
    {
        index = 10;
    }

    public void caseTInterface(TInterface node)
    {
        index = 11;
    }

    public void caseTVoid(TVoid node)
    {
        index = 12;
    }

    public void caseTBoolean(TBoolean node)
    {
        index = 13;
    }

    public void caseTByte(TByte node)
    {
        index = 14;
    }

    public void caseTShort(TShort node)
    {
        index = 15;
    }

    public void caseTChar(TChar node)
    {
        index = 16;
    }

    public void caseTInt(TInt node)
    {
        index = 17;
    }

    public void caseTLong(TLong node)
    {
        index = 18;
    }

    public void caseTFloat(TFloat node)
    {
        index = 19;
    }

    public void caseTDouble(TDouble node)
    {
        index = 20;
    }

    public void caseTNullType(TNullType node)
    {
        index = 21;
    }

    public void caseTUnknown(TUnknown node)
    {
        index = 22;
    }

    public void caseTExtends(TExtends node)
    {
        index = 23;
    }

    public void caseTImplements(TImplements node)
    {
        index = 24;
    }

    public void caseTBreakpoint(TBreakpoint node)
    {
        index = 25;
    }

    public void caseTCase(TCase node)
    {
        index = 26;
    }

    public void caseTCatch(TCatch node)
    {
        index = 27;
    }

    public void caseTCmp(TCmp node)
    {
        index = 28;
    }

    public void caseTCmpg(TCmpg node)
    {
        index = 29;
    }

    public void caseTCmpl(TCmpl node)
    {
        index = 30;
    }

    public void caseTDefault(TDefault node)
    {
        index = 31;
    }

    public void caseTEntermonitor(TEntermonitor node)
    {
        index = 32;
    }

    public void caseTExitmonitor(TExitmonitor node)
    {
        index = 33;
    }

    public void caseTFrom(TFrom node)
    {
        index = 34;
    }

    public void caseTGoto(TGoto node)
    {
        index = 35;
    }

    public void caseTIf(TIf node)
    {
        index = 36;
    }

    public void caseTInstanceof(TInstanceof node)
    {
        index = 37;
    }

    public void caseTInterfaceinvoke(TInterfaceinvoke node)
    {
        index = 38;
    }

    public void caseTLengthof(TLengthof node)
    {
        index = 39;
    }

    public void caseTLookupswitch(TLookupswitch node)
    {
        index = 40;
    }

    public void caseTNeg(TNeg node)
    {
        index = 41;
    }

    public void caseTNew(TNew node)
    {
        index = 42;
    }

    public void caseTNewarray(TNewarray node)
    {
        index = 43;
    }

    public void caseTNewmultiarray(TNewmultiarray node)
    {
        index = 44;
    }

    public void caseTNop(TNop node)
    {
        index = 45;
    }

    public void caseTRet(TRet node)
    {
        index = 46;
    }

    public void caseTReturn(TReturn node)
    {
        index = 47;
    }

    public void caseTSpecialinvoke(TSpecialinvoke node)
    {
        index = 48;
    }

    public void caseTStaticinvoke(TStaticinvoke node)
    {
        index = 49;
    }

    public void caseTTableswitch(TTableswitch node)
    {
        index = 50;
    }

    public void caseTThrow(TThrow node)
    {
        index = 51;
    }

    public void caseTThrows(TThrows node)
    {
        index = 52;
    }

    public void caseTTo(TTo node)
    {
        index = 53;
    }

    public void caseTVirtualinvoke(TVirtualinvoke node)
    {
        index = 54;
    }

    public void caseTWith(TWith node)
    {
        index = 55;
    }

    public void caseTNull(TNull node)
    {
        index = 56;
    }

    public void caseTComma(TComma node)
    {
        index = 57;
    }

    public void caseTLBrace(TLBrace node)
    {
        index = 58;
    }

    public void caseTRBrace(TRBrace node)
    {
        index = 59;
    }

    public void caseTSemicolon(TSemicolon node)
    {
        index = 60;
    }

    public void caseTLBracket(TLBracket node)
    {
        index = 61;
    }

    public void caseTRBracket(TRBracket node)
    {
        index = 62;
    }

    public void caseTLParen(TLParen node)
    {
        index = 63;
    }

    public void caseTRParen(TRParen node)
    {
        index = 64;
    }

    public void caseTColon(TColon node)
    {
        index = 65;
    }

    public void caseTDot(TDot node)
    {
        index = 66;
    }

    public void caseTQuote(TQuote node)
    {
        index = 67;
    }

    public void caseTColonEquals(TColonEquals node)
    {
        index = 68;
    }

    public void caseTEquals(TEquals node)
    {
        index = 69;
    }

    public void caseTAnd(TAnd node)
    {
        index = 70;
    }

    public void caseTOr(TOr node)
    {
        index = 71;
    }

    public void caseTXor(TXor node)
    {
        index = 72;
    }

    public void caseTMod(TMod node)
    {
        index = 73;
    }

    public void caseTCmpeq(TCmpeq node)
    {
        index = 74;
    }

    public void caseTCmpne(TCmpne node)
    {
        index = 75;
    }

    public void caseTCmpgt(TCmpgt node)
    {
        index = 76;
    }

    public void caseTCmpge(TCmpge node)
    {
        index = 77;
    }

    public void caseTCmplt(TCmplt node)
    {
        index = 78;
    }

    public void caseTCmple(TCmple node)
    {
        index = 79;
    }

    public void caseTShl(TShl node)
    {
        index = 80;
    }

    public void caseTShr(TShr node)
    {
        index = 81;
    }

    public void caseTUshr(TUshr node)
    {
        index = 82;
    }

    public void caseTPlus(TPlus node)
    {
        index = 83;
    }

    public void caseTMinus(TMinus node)
    {
        index = 84;
    }

    public void caseTMult(TMult node)
    {
        index = 85;
    }

    public void caseTDiv(TDiv node)
    {
        index = 86;
    }

    public void caseTFullIdentifier(TFullIdentifier node)
    {
        index = 87;
    }

    public void caseTQuotedName(TQuotedName node)
    {
        index = 88;
    }

    public void caseTIdentifier(TIdentifier node)
    {
        index = 89;
    }

    public void caseTAtIdentifier(TAtIdentifier node)
    {
        index = 90;
    }

    public void caseTBoolConstant(TBoolConstant node)
    {
        index = 91;
    }

    public void caseTIntegerConstant(TIntegerConstant node)
    {
        index = 92;
    }

    public void caseTFloatConstant(TFloatConstant node)
    {
        index = 93;
    }

    public void caseTStringConstant(TStringConstant node)
    {
        index = 94;
    }

    public void caseEOF(EOF node)
    {
        index = 95;
    }
}
