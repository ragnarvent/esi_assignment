package rentit.com.common.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlTransient
public class ResourceSupport extends org.springframework.hateoas.ResourceSupport{
	
	@XmlElement(name="xlink", namespace = Link.ATOM_NAMESPACE)
	@JsonProperty("xlinks")
	private final List<ExtendedLink> xlinks;
	
	public ResourceSupport(){
		super();
		this.xlinks = new ArrayList<>();
	}
	
	public void add(Link link){
		if(link instanceof ExtendedLink)
			this.xlinks.add((ExtendedLink) link);
		else super.add(link);
	}
	
	public List<ExtendedLink> getXlinks(){
		return Collections.unmodifiableList(xlinks);
	}
	
	public void clearXlinks(){
		this.xlinks.clear();
	}
	
	public ExtendedLink getXlink(String rel){
		//As proper hashCode and equals are undefined for ExtendedLink, 
		//HttpMethod parameter will not be considered in the comparison.
		return this.xlinks.stream().filter(xl->xl.getRel().equals(rel)).findFirst().get();
	}
	
}
