
package org.jboss.essc.web.rest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.jboss.essc.web.model.Product;

/**
 *
 *  @author Ondrej Zizka
 */
public class ProductSimpleAdapter extends XmlAdapter<String, Product> {
    @Override
    public Product unmarshal(String str) throws Exception {
        return Product.formString(str);
    }

    @Override
    public String marshal(Product prod) throws Exception {
        if( prod == null )  return null;
        return prod.toString();
    }
}
