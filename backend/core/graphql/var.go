package graphql

func castToStringSlice(i interface{}) []string {
	d := i.([]interface{})

	xs := make([]string, 0, len(d))
	for _, v := range d {
		xs = append(xs, v.(string))
	}

	return xs
}