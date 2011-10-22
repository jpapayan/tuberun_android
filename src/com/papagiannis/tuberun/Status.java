package com.papagiannis.tuberun;

import java.io.Serializable;

public class Status implements Serializable
{
	private static final long serialVersionUID = 1L;
	public LineType line_name;
    public String short_status;
    public String long_status;
    public Status(LineType name,String short_status, String long_status)
    {
        this.line_name = name;
        this.short_status = short_status;
        this.long_status = long_status;
    }
    public Status(String name, String short_status, String long_status)
    {
        this.line_name = LinePresentation.getLineTypeRespresentation(name);
        this.short_status = short_status;
        this.long_status = long_status;
    }

}
