package emailvalidator4j.parser;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import emailvalidator4j.lexer.EmailLexer;
import emailvalidator4j.parser.exception.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class DomainPartTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @UseDataProvider("invalidDomainParts")
    public void invalidPartThrowsException(Class type, String domainPart) throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser(domainPart);
        exception.expect(type);
        parser.parse(domainPart);
    }

    @DataProvider
    public static Object[][] invalidDomainParts() {
        //No existence of Tokens.AT is controlled in a superior class
        return new Object[][]{
                {DotAtStart.class, "@.atstart"},
                {DomainHyphen.class, "@-atstart"},
                {ExpectedATEXT.class, "@;atstart"},
                {DomainNotAllowedCharacter.class, "@/atstart"},
                {ConsecutiveDots.class, "@at..start"},
                {ConsecutiveCRLF.class, "@test\r\n\r\nat"},
                {CRLFAtEnd.class, "@test\r\nat"},
                {CRWithoutLF.class, "@test\rat"},
                {ATEXTAfterCFWS.class, "@test\r\n at"},
                {ExpectedCTEXT.class, "@test\r\n \n"},
                {UnclosedComment.class, "@a(comment"},
                {DomainNotAllowedCharacter.class, "@a,start"},
                {ConsecutiveAT.class, "@@start"},
                {ExpectedATEXT.class, "@at[start"},
                {DomainHyphen.class, "@atstart-.com"},
                {ExpectedATEXT.class, "@atst\\art.com"},
        };
    }

    @Test
    @UseDataProvider("invalidDomainLiteralParts")
    public void invalidDomainLiteralExceptions(Class type, String literalPart) throws InvalidEmail {
        DomainPart parser = this.getDomainPartParser(literalPart);
        exception.expect(type);
        parser.parse(literalPart);
    }

    @DataProvider
    public static Object[][] invalidDomainLiteralParts() {
        return new Object[][]{
                {ExpectedDTEXT.class, "@[[127.0.0.1]"},
                {CRWithoutLF.class, "@[\r127.0.0.1]"}
        };
    }

    private DomainPart getDomainPartParser(String domainPart) {
        EmailLexer lexer = new EmailLexer();
        lexer.lex(domainPart);
        return new DomainPart(lexer);
    }
}