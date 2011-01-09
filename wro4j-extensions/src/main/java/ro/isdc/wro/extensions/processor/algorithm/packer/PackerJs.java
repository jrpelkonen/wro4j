/*
 *  Copyright 2010 Alex Objelean.
 */
package ro.isdc.wro.extensions.processor.algorithm.packer;

import java.io.IOException;
import java.io.InputStream;

import org.mozilla.javascript.RhinoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;


/**
 * Apply Packer compressor script using scriptEngine.
 *
 * @author Alex Objelean
 */
public class PackerJs {
  private static final Logger LOG = LoggerFactory.getLogger(PackerJs.class);

  /**
   * Initialize script builder for evaluation.
   */
  private RhinoScriptBuilder initScriptBuilder() {
    try {
      return RhinoScriptBuilder.newChain().evaluateChain(getStreamForBase2(), "base2.js").evaluateChain(
        getStreamForPacker(), "packer.js");
    } catch (final IOException ex) {
      throw new IllegalStateException("Failed reading init script", ex);
    }
  }

  /**
   * Override this method if you have a newer version of base2.js file.
   *
   * @return Stream for base2.js
   */
  protected InputStream getStreamForBase2() {
    return getClass().getResourceAsStream("base2.js");
  }

  /**
   * Override this method if you have a newer version of packer.js file.
   *
   * @return Stream for packer.js
   */
  protected InputStream getStreamForPacker() {
    return getClass().getResourceAsStream("packer.js");
  }

  /**
   * @param data js content to process.
   * @return packed js content.
   */
  public String pack(final String data)
    throws IOException {
    try {
      final StopWatch watch = new StopWatch();
      watch.start("init");
      final RhinoScriptBuilder builder = initScriptBuilder();
      watch.stop();
      watch.start("pack");

      final String packIt = buildPackScript(WroUtil.toJSMultiLineString(data));
      final Object result = builder.evaluate(packIt, "packerIt");
      watch.stop();
      LOG.debug(watch.prettyPrint());
      return String.valueOf(result);
    } catch (final RhinoException e) {
      throw new WroRuntimeException("Unable to evaluate the script because: " + e.getMessage(), e);
    }
  }

  /**
   * @param data script to pack.
   * @return Script used to pack and return the packed result.
   */
  protected String buildPackScript(final String data) {
    return "new Packer().pack(" + data + ", true, true);";
  }
}
