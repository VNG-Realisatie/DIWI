package com.vng.generic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class StringUtil
{
    public static String capatilizeFirstLetter(String input)
    {
        if(input == null || (input.length() == 0))
        {
            return input;
        }
        else if(input.length() == 1)
        {
            return input.toUpperCase();
        }
        else
        {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    }

    public static String unCapatilizeFirstLetter(String input)
    {
        if(input == null || (input.length() == 0))
        {
            return input;
        }
        else if(input.length() == 1)
        {
            return input.toLowerCase();
        }
        else
        {
            return input.substring(0, 1).toLowerCase() + input.substring(1);
        }
    }

    public static String splitOnCapitalLetter(String input, int noCapitalLetter, char splitChar)
    {
        int countCapital = 0;
        if(input == null || (input.length() == 0))
        {
            return input;
        }
        for(int i = 0; i < input.length(); ++i)
        {
            if(Character.isUpperCase(input.charAt(i)))
            {
                countCapital++;
            }
            if(countCapital >= noCapitalLetter)
            {
                //return split
                return input.substring(0, i) + splitChar + input.substring(i);
            }
        }
        return input;
    }


    public static String addSpaces(String strIn, int spaceWidth)
    {
        String ret = strIn;
        while(ret.length() < spaceWidth)
        {
            ret += " ";
        }
        return ret;
    }

    public static String formatDoubleWithExplicitZero(double d, int precision)
    {
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(precision);
        if( d == 0.0)
        {
            return "ZERO";
        }
        else
        {
            return formatter.format(d);
        }
    }

    public static String getFirstLineFromString(String str)
    {
        int newLineIndex = str.indexOf("\n");
        if(newLineIndex > 0)
        {
            return str.substring(0, newLineIndex);
        }
        else
        {
            return str;
        }

    }

    public static String printWithNull(Object o)
    {
        return Objects.toString(o);
    }

    public static String shortPrintBool(Boolean b)
    {
        if(b == null)
        {
            return "N";
        }
        else
        {
            if(b)
            {
                return "1";
            }
            else
            {
                return "0";
            }
        }
    }
}
