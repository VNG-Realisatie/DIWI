package com.vng.dal;

public class DalException extends Exception
{
    private static final long serialVersionUID = -965552537205906675L;

    public DalException()
    {
        super();
    }

    public DalException(String msg)
    {
        super(msg);
    }
}
