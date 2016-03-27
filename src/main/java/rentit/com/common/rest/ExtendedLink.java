package rentit.com.common.rest;

import javax.xml.bind.annotation.XmlType;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

@XmlType(name="xlink", namespace = Link.ATOM_NAMESPACE)
public class ExtendedLink extends Link{
	private static final long serialVersionUID = 6233032557436365087L;
	private HttpMethod method;
	private String _rel;
	
	public ExtendedLink(){}
	
	public ExtendedLink(String href, String rel, HttpMethod method){
		super(href, rel);
		this.method = method;
		this._rel = rel;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String get_rel() {
		return _rel;
	}
}
