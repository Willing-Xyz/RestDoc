package cn.willingxyz.restdoc.spring.examples.circularreference;

import lombok.Data;

@Data
public class CircularA {
    private String _name;
    private CircularB _circularB;
}
