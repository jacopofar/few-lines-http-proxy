This Java library,based on LittleProxy, is made to ease the development of HTTP proxies allowing to log and edit HTTP requests and responses with a few lines of codes.

To use it, implement the `BasicNavigationManipulator` interface with a class and start the proxy with:

`HttpFiltersSourceAdapter fs=new InspectorFilterSourceAdapter(new MyProxyClass());`
`DefaultHttpProxyServer.bootstrap()..withFiltersSource(fsadapter).withTransparent(true).start()`

Through chaining you can also define a port (default 8080) and other details, see LittleProxy documentation for more details.

HTTPS connections can pass but obviously cannot be inspected.

The library allows to edit the HTML responses as JSoup documents on the fly; two examples are provided:

* AliceToBob: replace "Alice" with "Bob" inside the page, applying a red and bold style to the edited text
* LogPages: logs all the requests and responses in a H2 database

