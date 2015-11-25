package ome.smuggler.core.msg;

/**
 * A message consumer may throw this to tell the channel's runtime to put the
 * message just received back in the channel and deliver it again later.
 * The channel used has to support this.
 */
public class RetryLater extends RuntimeException {

    private static final long serialVersionUID = 1L;

}
