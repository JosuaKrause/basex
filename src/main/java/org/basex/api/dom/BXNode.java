package org.basex.api.dom;

import static org.basex.util.Token.*;

import org.basex.data.*;
import org.basex.io.*;
import org.basex.query.iter.*;
import org.basex.query.util.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.util.*;
import org.w3c.dom.*;

/**
 * DOM - Node implementation.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public abstract class BXNode implements Node {
  /** Node type mapping (see {@link Data} interface). */
  private static final short[] TYPES = {
    Node.DOCUMENT_NODE, Node.ELEMENT_NODE, Node.TEXT_NODE, Node.ATTRIBUTE_NODE,
    Node.COMMENT_NODE, Node.PROCESSING_INSTRUCTION_NODE, Node.CDATA_SECTION_NODE,
    Node.DOCUMENT_FRAGMENT_NODE
  };
  /** Node name mapping (see {@link Data} interface). */
  private static final String[] NAMES = {
    "#document", null, "#text", null, "#comment", null, "#cdata-section",
    "#document-fragment"
  };
  /** Node reference. */
  final ANode node;

  /**
   * Constructor.
   * @param n node reference
   */
  BXNode(final ANode n) {
    node = n.deepCopy();
  }

  @Override
  public String getNodeName() {
    return NAMES[kind()];
  }

  @Override
  public final short getNodeType() {
    return TYPES[kind()];
  }

  /**
   * Returns a numeric value for the node kind.
   * @return node kind
   */
  int kind() {
    return node.kind();
  }

  @Override
  public String getNodeValue() {
    return null;
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public final BXNode cloneNode(final boolean deep) {
    return node.copy().toJava();
  }

  @Override
  public final short compareDocumentPosition(final Node other) {
    final int d = node.diff(((BXNode) other).node);
    return (short) (d < 0 ? -1 : d > 0 ? 1 : 0);
  }

  @Override
  public BXNNode getAttributes() {
    return null;
  }

  @Override
  public final String getBaseURI() {
    return IO.get(string(node.baseURI())).url();
  }

  @Override
  public BXNList getChildNodes() {
    return new BXNList(finish(node.children()));
  }

  @Override
  public BXNode getFirstChild() {
    return toJava(node.children().next().finish());
  }

  @Override
  public final BXNode getLastChild() {
    ANode n = null;
    for(final ANode t : node.children()) n = t;
    return toJava(n.finish());
  }

  @Override
  public String getNamespaceURI() {
    return null;
  }

  @Override
  public BXNode getNextSibling() {
    return toJava(node.followingSibling().next().finish());
  }

  @Override
  public BXNode getPreviousSibling() {
    return toJava(node.precedingSibling().next().finish());
  }

  @Override
  public final BXNode getParentNode() {
    return toJava(node.parent());
  }

  /**
   * Returns a Java node for the specified argument or {@code null}.
   * @param n node instance
   * @return resulting node
   */
  private static BXNode toJava(final ANode n) {
    return n != null ? n.toJava() : null;
  }

  @Override
  public final boolean hasChildNodes() {
    return getFirstChild() != null;
  }

  @Override
  public final boolean isSameNode(final Node other) {
    return this == other;
  }

  @Override
  public BXDoc getOwnerDocument() {
    ANode n = node;
    for(ANode p; (p = n.parent()) != null;) n = p;
    return n.type == NodeType.DOC ? (BXDoc) n.toJava() : null;
  }

  @Override
  public final boolean hasAttributes() {
    return getAttributes().getLength() != 0;
  }

  @Override
  public final Object getFeature(final String feature, final String version) {
    return null;
  }

  @Override
  public final String getPrefix() {
    return null;
  }

  @Override
  public final String getTextContent() {
    return string(node.string());
  }

  @Override
  public final BXNode appendChild(final Node newChild) {
    throw readOnly();
  }

  @Override
  public final Object getUserData(final String key) {
    return null;
  }

  @Override
  public final boolean isSupported(final String feature, final String version) {
    return false;
  }

  @Override
  public final BXNode insertBefore(final Node newChild, final Node refChild) {
    throw readOnly();
  }

  @Override
  public final boolean isDefaultNamespace(final String namespaceURI) {
    throw Util.notimplemented();
  }

  @Override
  public final boolean isEqualNode(final Node cmp) {
    throw Util.notimplemented();
  }

  @Override
  public final String lookupNamespaceURI(final String prefix) {
    throw Util.notimplemented();
  }

  @Override
  public final String lookupPrefix(final String namespaceURI) {
    throw Util.notimplemented();
  }

  @Override
  public final void normalize() {
    throw readOnly();
  }

  @Override
  public final BXNode removeChild(final Node oldChild) {
    throw readOnly();
  }

  @Override
  public final BXNode replaceChild(final Node newChild, final Node oldChild) {
    throw readOnly();
  }

  @Override
  public final void setNodeValue(final String nodeValue) {
    throw readOnly();
  }

  @Override
  public final void setPrefix(final String prefix) {
    throw readOnly();
  }

  @Override
  public final void setTextContent(final String textContent) {
    throw readOnly();
  }

  @Override
  public final Object setUserData(final String key, final Object dat,
      final UserDataHandler handler) {
    throw readOnly();
  }

  @Override
  public final String toString() {
    return '[' + getNodeName() + ": " + getNodeValue() + ']';
  }

  /**
   * Returns all nodes with the given tag name.
   * @param tag tag name
   * @return nodes
   */
  final BXNList getElements(final String tag) {
    final ANodeList nb = new ANodeList();
    final AxisIter ai = node.descendant();
    final byte[] nm = tag.equals("*") ? null : token(tag);
    for(ANode n; (n = ai.next()) != null;) {
      if(n.type == NodeType.ELM && (nm == null || eq(nm, n.name()))) nb.add(n.finish());
    }
    return new BXNList(nb);
  }

  /**
   * Returns a node cache with the specified nodes.
   * @param ai axis iterator
   * @return node cache
   */
  static ANodeList finish(final AxisIter ai) {
    final ANodeList nl = new ANodeList();
    for(ANode n; (n = ai.next()) != null;) nl.add(n.finish());
    return nl;
  }

  /**
   * Returns the XQuery node.
   * @return xquery node
   */
  public final ANode getNod() {
    return node;
  }

  /**
   * Throws a DOM modification exception.
   * @return DOM exception
   */
  static final DOMException readOnly() {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
        "DOM implementation is read-only.");
  }
}
