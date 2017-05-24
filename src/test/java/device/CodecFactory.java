/**
 * 
 */
package device;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * @author jy.zhao
 * 
 */
public class CodecFactory extends DemuxingProtocolCodecFactory {
//	private MsgDecoder decoder;
//
//	private MsgEncoder encoder;

//	public CodecFactory(MsgDecoder decoder,
//			MsgEncoder encoder) {
//		this.decoder = decoder;
//		this.encoder = encoder;
//
//		addMessageDecoder(MsgDecoder.class);
//		addMessageEncoder(String.class, MsgEncoder.class);
//	}
	
	public CodecFactory() {
		addMessageDecoder(MsgDecoder.class);
		addMessageEncoder(String.class, MsgEncoder.class);
	}
}
